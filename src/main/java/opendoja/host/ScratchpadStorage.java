package opendoja.host;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

final class ScratchpadStorage {
    private static final int HEADER_BYTES = 64;
    private static final int HEADER_ENTRIES = 16;

    private final Path legacyRoot;
    private final Path packedFile;
    private final int[] configuredSizes;

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
        PackedLayout layout = PackedLayout.load(packedFile, configuredSizes);
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
            file.seek(0L);
            file.write(layout.headerBytes());
        }
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

        static PackedLayout load(Path packedFile, int[] configuredSizes) throws IOException {
            if (Files.exists(packedFile)) {
                long fileSize = Files.size(packedFile);
                if (fileSize >= HEADER_BYTES) {
                    PackedLayout parsed = parseExisting(packedFile, fileSize, configuredSizes);
                    if (parsed != null) {
                        return parsed;
                    }
                }
                if (fileSize > 0L) {
                    return single();
                }
            }
            if (configuredSizes.length > 0) {
                return configured(configuredSizes);
            }
            return single();
        }

        private static PackedLayout parseExisting(Path packedFile, long fileSize, int[] configuredSizes) throws IOException {
            byte[] header = Files.readAllBytes(packedFile);
            if (header.length < HEADER_BYTES) {
                return null;
            }
            ByteBuffer buffer = ByteBuffer.wrap(header, 0, HEADER_BYTES);
            int[] sizes = new int[HEADER_ENTRIES];
            long[] offsets = new long[HEADER_ENTRIES];
            long total = HEADER_BYTES;
            long offset = HEADER_BYTES;
            for (int i = 0; i < HEADER_ENTRIES; i++) {
                int normalized = normalizeHeaderSize(buffer.getInt(), (int) fileSize, configuredSizes, i);
                if (normalized < 0) {
                    return null;
                }
                sizes[i] = normalized;
                offsets[i] = offset;
                total += normalized;
                offset += normalized;
            }
            if (total != fileSize) {
                return null;
            }
            return new PackedLayout(sizes, offsets, true, false);
        }

        private static PackedLayout configured(int[] configuredSizes) {
            int[] sizes = new int[HEADER_ENTRIES];
            long[] offsets = new long[HEADER_ENTRIES];
            long offset = HEADER_BYTES;
            for (int i = 0; i < HEADER_ENTRIES; i++) {
                int size = i < configuredSizes.length ? Math.max(0, configuredSizes[i]) : 0;
                sizes[i] = size;
                offsets[i] = offset;
                offset += size;
            }
            return new PackedLayout(sizes, offsets, true, true);
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
            return offsets.length == 0 ? HEADER_BYTES : offsets[offsets.length - 1] + sizes[sizes.length - 1];
        }

        byte[] headerBytes() {
            ByteBuffer buffer = ByteBuffer.allocate(HEADER_BYTES);
            for (int i = 0; i < HEADER_ENTRIES; i++) {
                int size = i < sizes.length ? sizes[i] : 0;
                buffer.putInt(size > 0 ? size : -1);
            }
            return buffer.array();
        }

        ScratchpadAccess access(int index, long position, long requestedLength) {
            int safeIndex = index < 0 || index >= HEADER_ENTRIES ? HEADER_ENTRIES : index;
            long normalizedPosition = Math.max(0L, position);
            if (safeIndex >= HEADER_ENTRIES) {
                return new ScratchpadAccess(HEADER_BYTES + normalizedPosition, 0L);
            }
            int segmentSize = sizes[safeIndex];
            long clampedPosition = Math.min(normalizedPosition, segmentSize);
            long available = Math.max(0L, segmentSize - clampedPosition);
            long boundedLength = requestedLength < 0L ? available : Math.min(requestedLength, available);
            return new ScratchpadAccess(offsets[safeIndex] + clampedPosition, boundedLength);
        }

        private static int normalizeHeaderSize(int raw, int seedLength, int[] configuredSizes, int index) {
            int chosen = raw;
            int reversed = Integer.reverseBytes(raw);
            int configured = index < configuredSizes.length ? configuredSizes[index] : Integer.MIN_VALUE;
            if (configured != Integer.MIN_VALUE) {
                if (reversed == configured && chosen != configured) {
                    chosen = reversed;
                }
            } else if (chosen > seedLength || chosen < -1) {
                chosen = reversed;
            }
            if (chosen > seedLength || chosen < -1) {
                return -1;
            }
            return chosen == -1 ? 0 : chosen;
        }
    }
}
