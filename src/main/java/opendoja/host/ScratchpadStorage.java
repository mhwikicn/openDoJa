package opendoja.host;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

final class ScratchpadStorage {
    private static final int HEADER_BYTES = 64;
    private static final int HEADER_ENTRIES = 16;

    private final Path legacyRoot;
    private final Path packedFile;
    private final int[] configuredSizes;
    private volatile boolean warnedHeaderMismatch;

    ScratchpadStorage(Path legacyRoot, Path packedFile, int[] configuredSizes) {
        this.legacyRoot = legacyRoot;
        this.packedFile = packedFile;
        this.configuredSizes = configuredSizes == null ? new int[0] : configuredSizes.clone();
    }

    void initialize() throws IOException {
        if (packedFile != null) {
            Path parent = packedFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            return;
        }
        if (legacyRoot != null) {
            Files.createDirectories(legacyRoot);
        }
    }

    InputStream openInput(int index, long position, long length) throws IOException {
        if (packedFile == null) {
            return ScratchpadStreams.openInput(legacyFile(index), position, length);
        }
        ScratchpadAccess access = resolvePackedAccess(index, position, length);
        return ScratchpadStreams.openInput(packedFile, access.offset(), access.length());
    }

    OutputStream openOutput(int index, long position, long length) throws IOException {
        if (packedFile == null) {
            return ScratchpadStreams.openOutput(legacyFile(index), position, length);
        }
        ScratchpadAccess access = resolvePackedAccess(index, position, length);
        return ScratchpadStreams.openOutput(packedFile, access.offset(), access.length());
    }

    private Path legacyFile(int index) {
        return legacyRoot.resolve("sp-" + index + ".bin");
    }

    private ScratchpadAccess resolvePackedAccess(int index, long position, long requestedLength) throws IOException {
        PackedLayout layout = loadPackedLayout();
        if (layout.packed()) {
            ensurePackedFile(layout);
            return layout.access(index, position, requestedLength);
        }
        long normalizedPosition = Math.max(0L, position);
        if (index != 0) {
            OpenDoJaLog.warn(ScratchpadStorage.class,
                    () -> "Packed .sp backing " + packedFile + " has no segment table; treating scratchpad index "
                            + index + " as index 0");
        }
        return new ScratchpadAccess(normalizedPosition, requestedLength);
    }

    private void ensurePackedFile(PackedLayout layout) throws IOException {
        if (!layout.synthetic()) {
            return;
        }
        Path parent = packedFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (Files.exists(packedFile) && Files.size(packedFile) > 0L) {
            return;
        }
        try (RandomAccessFile file = new RandomAccessFile(packedFile.toFile(), "rw")) {
            file.setLength(layout.totalBytes());
        }
    }

    private PackedLayout loadPackedLayout() throws IOException {
        if (packedFile == null) {
            return PackedLayout.single();
        }
        if (Files.exists(packedFile)) {
            long fileSize = Files.size(packedFile);
            if (configuredSizes.length > 0) {
                return configuredLayoutFor(fileSize);
            }
            if (fileSize > 0L) {
                return PackedLayout.single();
            }
        }
        if (configuredSizes.length > 0) {
            return PackedLayout.configured(configuredSizes, 0L, true);
        }
        return PackedLayout.single();
    }

    private PackedLayout configuredLayoutFor(long fileSize) {
        long declaredPayloadBytes = declaredPayloadBytes(configuredSizes);
        if (fileSize == declaredPayloadBytes + HEADER_BYTES) {
            return PackedLayout.configured(configuredSizes, HEADER_BYTES, false);
        }
        if (fileSize == declaredPayloadBytes) {
            return PackedLayout.configured(configuredSizes, 0L, false);
        }
        warnConfiguredSizeMismatch(fileSize, declaredPayloadBytes);
        return PackedLayout.configured(configuredSizes, 0L, false);
    }

    private void warnConfiguredSizeMismatch(long fileSize, long declaredPayloadBytes) {
        if (warnedHeaderMismatch) {
            return;
        }
        warnedHeaderMismatch = true;
        OpenDoJaLog.warn(ScratchpadStorage.class,
                () -> "Packed .sp backing " + packedFile
                        + " declares " + declaredPayloadBytes
                        + " bytes but file size is " + fileSize
                        + "; expected either " + declaredPayloadBytes
                        + " (raw) or " + (declaredPayloadBytes + HEADER_BYTES)
                        + " (with 64-byte dojaemu header)");
    }

    private static long declaredPayloadBytes(int[] sizes) {
        long total = 0L;
        for (int size : sizes) {
            total += Math.max(0, size);
        }
        return total;
    }

    private record ScratchpadAccess(long offset, long length) {
    }

    private static final class PackedLayout {
        private final int[] sizes;
        private final long[] offsets;
        private final boolean packed;
        private final boolean synthetic;

        private PackedLayout(int[] sizes, long[] offsets, boolean packed, boolean synthetic) {
            this.sizes = sizes;
            this.offsets = offsets;
            this.packed = packed;
            this.synthetic = synthetic;
        }

        private static PackedLayout configured(int[] configuredSizes, long dataStart, boolean synthetic) {
            int[] sizes = new int[HEADER_ENTRIES];
            long[] offsets = new long[HEADER_ENTRIES];
            long offset = Math.max(0L, dataStart);
            for (int i = 0; i < HEADER_ENTRIES; i++) {
                int size = i < configuredSizes.length ? Math.max(0, configuredSizes[i]) : 0;
                sizes[i] = size;
                offsets[i] = offset;
                offset += size;
            }
            return new PackedLayout(sizes, offsets, true, synthetic);
        }

        private static PackedLayout single() {
            return new PackedLayout(new int[0], new long[0], false, false);
        }

        boolean packed() {
            return packed;
        }

        boolean synthetic() {
            return synthetic;
        }

        long totalBytes() {
            if (!packed) {
                return 0L;
            }
            return offsets.length == 0 ? 0L : offsets[offsets.length - 1] + sizes[sizes.length - 1];
        }

        ScratchpadAccess access(int index, long position, long requestedLength) {
            int safeIndex = index < 0 || index >= HEADER_ENTRIES ? HEADER_ENTRIES : index;
            long normalizedPosition = Math.max(0L, position);
            if (safeIndex >= HEADER_ENTRIES) {
                return new ScratchpadAccess(normalizedPosition, 0L);
            }
            int segmentSize = sizes[safeIndex];
            long clampedPosition = Math.min(normalizedPosition, segmentSize);
            long available = Math.max(0L, segmentSize - clampedPosition);
            long boundedLength = requestedLength < 0L ? available : Math.min(requestedLength, available);
            return new ScratchpadAccess(offsets[safeIndex] + clampedPosition, boundedLength);
        }
    }
}
