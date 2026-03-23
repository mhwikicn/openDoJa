package opendoja.host;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public final class DesktopSurface {
    private static final long INPUT_WOKEN_SYNC_UNLOCK_FLOOR_NANOS = (1_000_000_000L / 60L);
    private BufferedImage image;
    private int backgroundColor = 0xFF000000;
    private Consumer<BufferedImage> repaintHook;
    private float[] depthBuffer;
    private boolean depthFrameActive;
    private long syncUnlockIntervalNanos;
    private long lastFlushNanos;
    private volatile Thread syncUnlockWaitThread;
    private volatile boolean syncUnlockWakeRequested;

    public DesktopSurface(int width, int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public void resize(int width, int height) {
        if (image.getWidth() == width && image.getHeight() == height) {
            return;
        }
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        this.image = resized;
        this.depthBuffer = null;
        this.depthFrameActive = false;
    }

    public BufferedImage image() {
        return image;
    }

    public int width() {
        return image.getWidth();
    }

    public int height() {
        return image.getHeight();
    }

    public int backgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setRepaintHook(Consumer<BufferedImage> repaintHook) {
        this.repaintHook = repaintHook;
    }

    public boolean hasRepaintHook() {
        return repaintHook != null;
    }

    public boolean immediatePresentationEnabled() {
        return repaintHook != null && syncUnlockIntervalNanos > 0L;
    }

    public void setSyncUnlockIntervalNanos(long syncUnlockIntervalNanos) {
        this.syncUnlockIntervalNanos = Math.max(0L, syncUnlockIntervalNanos);
        if (this.syncUnlockIntervalNanos == 0L) {
            this.lastFlushNanos = 0L;
        }
    }

    long syncUnlockIntervalNanos() {
        return syncUnlockIntervalNanos;
    }

    void wakeSyncUnlockWait() {
        Thread waitThread = syncUnlockWaitThread;
        if (waitThread != null) {
            syncUnlockWakeRequested = true;
            java.util.concurrent.locks.LockSupport.unpark(waitThread);
        }
    }

    public synchronized float[] depthBufferForFrame() {
        int pixelCount = image.getWidth() * image.getHeight();
        if (depthBuffer == null || depthBuffer.length != pixelCount) {
            depthBuffer = new float[pixelCount];
            depthFrameActive = false;
        }
        if (!depthFrameActive) {
            Arrays.fill(depthBuffer, Float.NEGATIVE_INFINITY);
            depthFrameActive = true;
        }
        return depthBuffer;
    }

    public synchronized void endDepthFrame() {
        depthFrameActive = false;
    }

    public void flush(BufferedImage presentedFrame) {
        if (syncUnlockIntervalNanos > 0L && repaintHook != null) {
            long now = System.nanoTime();
            long elapsed = now - lastFlushNanos;
            if (lastFlushNanos != 0L && elapsed < syncUnlockIntervalNanos) {
                long normalTargetNanos = lastFlushNanos + syncUnlockIntervalNanos;
                long expeditedTargetNanos = lastFlushNanos
                        + java.lang.Math.min(syncUnlockIntervalNanos, INPUT_WOKEN_SYNC_UNLOCK_FLOOR_NANOS);
                boolean expedited = false;
                while (true) {
                    long targetNanos = expedited ? expeditedTargetNanos : normalTargetNanos;
                    now = System.nanoTime();
                    long remainingNanos = targetNanos - now;
                    if (remainingNanos <= 0L) {
                        break;
                    }
                    try {
                        syncUnlockWaitThread = Thread.currentThread();
                        java.util.concurrent.locks.LockSupport.parkNanos(remainingNanos);
                    } finally {
                        syncUnlockWaitThread = null;
                    }
                    if (syncUnlockWakeRequested) {
                        syncUnlockWakeRequested = false;
                        expedited = true;
                    }
                }
                syncUnlockWakeRequested = false;
            }
            lastFlushNanos = now;
        }
        present(presentedFrame);
    }

    public void presentImmediately(BufferedImage presentedFrame) {
        // Immediate direct-canvas presentation is only a visual snapshot. It is not a
        // definitive frame boundary, so keep the shared depth frame alive for any later 3D
        // submissions that still belong to the same game frame.
        if (repaintHook != null) {
            repaintHook.accept(presentedFrame);
        }
    }

    private void present(BufferedImage presentedFrame) {
        endDepthFrame();
        if (repaintHook != null) {
            repaintHook.accept(presentedFrame);
        }
    }

    @Override
    public String toString() {
        return "DesktopSurface{" + image.getWidth() + "x" + image.getHeight() + ", background=" + backgroundColor + "}";
    }
}
