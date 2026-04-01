package opendoja.host;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

public final class DesktopSurface {
    private static final long PRESENT_SYNC_INTERVAL_NANOS = 16_000_000L;
    private BufferedImage image;
    private int backgroundColor = 0xFF000000;
    private Consumer<BufferedImage> repaintHook;
    private float[] depthBuffer;
    private boolean depthFrameActive;
    private long nextRenderSyncNanos;

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
        this.nextRenderSyncNanos = 0L;
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

    public synchronized void waitForRenderSync(long intervalNanos) {
        if (intervalNanos <= 0L || repaintHook == null) {
            return;
        }
        long now = System.nanoTime();
        long target = nextRenderSyncNanos == 0L ? now + intervalNanos : nextRenderSyncNanos + intervalNanos;
        if (target > now) {
            LockSupport.parkNanos(target - now);
            nextRenderSyncNanos = target;
            return;
        }
        nextRenderSyncNanos = now;
    }

    public void flush(BufferedImage presentedFrame) {
        flush(presentedFrame, true);
    }

    public void flush(BufferedImage presentedFrame, boolean paced) {
        if (paced) {
            // The official emulator exposes a separate graphics sync interval of `16000us`.
            // Keep the paced direct-present path on that cadence, while callers that already
            // performed an explicit sync wait pass `paced=false` to avoid double throttling.
            waitForRenderSync(PRESENT_SYNC_INTERVAL_NANOS);
        }
        present(presentedFrame);
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
