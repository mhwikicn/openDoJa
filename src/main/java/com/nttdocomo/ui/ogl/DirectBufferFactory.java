package com.nttdocomo.ui.ogl;

import com.nttdocomo.ui.ogl.math.Matrix4f;

import java.nio.ByteOrder;

/**
 * Allocates direct buffers for the DoJa OpenGL utility APIs.
 */
public final class DirectBufferFactory {
    private static final DirectBufferFactory FACTORY = new DirectBufferFactory();

    private DirectBufferFactory() {
    }

    /**
     * Returns the singleton factory instance.
     *
     * @return the shared factory
     */
    public static DirectBufferFactory getFactory() {
        return FACTORY;
    }

    /**
     * Allocates a byte buffer of the specified length.
     *
     * @param size the number of elements
     * @return the allocated buffer
     */
    public ByteBuffer allocateByteBuffer(int size) {
        return new ArrayByteBuffer(new byte[checkedSize(size)]);
    }

    /**
     * Allocates a byte buffer initialized from a byte array.
     *
     * @param initialData the initial data
     * @return the allocated buffer
     */
    public ByteBuffer allocateByteBuffer(byte[] initialData) {
        if (initialData == null) {
            throw new NullPointerException("initialData");
        }
        return new ArrayByteBuffer(initialData.clone());
    }

    /**
     * Allocates a byte buffer initialized from another system buffer.
     *
     * @param buff the source buffer
     * @return the allocated buffer
     */
    public ByteBuffer allocateByteBuffer(ByteBuffer buff) {
        ArrayByteBuffer source = requireByteBuffer(buff);
        return new ArrayByteBuffer(source.values.clone());
    }

    /**
     * Allocates a short buffer of the specified length.
     *
     * @param size the number of elements
     * @return the allocated buffer
     */
    public ShortBuffer allocateShortBuffer(int size) {
        return new ArrayShortBuffer(new short[checkedSize(size)]);
    }

    /**
     * Allocates a short buffer initialized from bytes.
     *
     * @param initialData the initial bytes
     * @return the allocated buffer
     */
    public ShortBuffer allocateShortBuffer(byte[] initialData) {
        if (initialData == null) {
            throw new NullPointerException("initialData");
        }
        short[] values = new short[initialData.length / 2];
        for (int i = 0; i < values.length; i++) {
            int offset = i * 2;
            values[i] = (short) (((initialData[offset] & 0xFF) << 8) | (initialData[offset + 1] & 0xFF));
        }
        return new ArrayShortBuffer(values);
    }

    /**
     * Allocates a short buffer initialized from another system buffer.
     *
     * @param buff the source buffer
     * @return the allocated buffer
     */
    public ShortBuffer allocateShortBuffer(ShortBuffer buff) {
        ArrayShortBuffer source = requireShortBuffer(buff);
        return new ArrayShortBuffer(source.values.clone());
    }

    /**
     * Allocates an int buffer of the specified length.
     *
     * @param size the number of elements
     * @return the allocated buffer
     */
    public IntBuffer allocateIntBuffer(int size) {
        return new ArrayIntBuffer(new int[checkedSize(size)]);
    }

    /**
     * Allocates an int buffer initialized from bytes.
     *
     * @param initialData the initial bytes
     * @return the allocated buffer
     */
    public IntBuffer allocateIntBuffer(byte[] initialData) {
        if (initialData == null) {
            throw new NullPointerException("initialData");
        }
        int[] values = new int[initialData.length / 4];
        for (int i = 0; i < values.length; i++) {
            int offset = i * 4;
            values[i] = ((initialData[offset] & 0xFF) << 24)
                    | ((initialData[offset + 1] & 0xFF) << 16)
                    | ((initialData[offset + 2] & 0xFF) << 8)
                    | (initialData[offset + 3] & 0xFF);
        }
        return new ArrayIntBuffer(values);
    }

    /**
     * Allocates an int buffer initialized from another system buffer.
     *
     * @param buff the source buffer
     * @return the allocated buffer
     */
    public IntBuffer allocateIntBuffer(IntBuffer buff) {
        ArrayIntBuffer source = requireIntBuffer(buff);
        return new ArrayIntBuffer(source.values.clone());
    }

    /**
     * Allocates a float buffer of the specified length.
     *
     * @param size the number of elements
     * @return the allocated buffer
     */
    public FloatBuffer allocateFloatBuffer(int size) {
        return new ArrayFloatBuffer(new float[checkedSize(size)]);
    }

    /**
     * Allocates a float buffer initialized from bytes.
     *
     * @param initialData the initial bytes
     * @return the allocated buffer
     */
    public FloatBuffer allocateFloatBuffer(byte[] initialData) {
        if (initialData == null) {
            throw new NullPointerException("initialData");
        }
        float[] values = new float[initialData.length / 4];
        for (int i = 0; i < values.length; i++) {
            int offset = i * 4;
            int bits = ((initialData[offset] & 0xFF) << 24)
                    | ((initialData[offset + 1] & 0xFF) << 16)
                    | ((initialData[offset + 2] & 0xFF) << 8)
                    | (initialData[offset + 3] & 0xFF);
            values[i] = Float.intBitsToFloat(bits);
        }
        return new ArrayFloatBuffer(values);
    }

    /**
     * Allocates a float buffer initialized from another system buffer.
     *
     * @param buff the source buffer
     * @return the allocated buffer
     */
    public FloatBuffer allocateFloatBuffer(FloatBuffer buff) {
        ArrayFloatBuffer source = requireFloatBuffer(buff);
        return new ArrayFloatBuffer(source.values.clone());
    }

    /**
     * Allocates a byte buffer initialized from short values.
     *
     * @param initialData the initial values
     * @return the allocated buffer
     */
    public ByteBuffer allocateByteBuffer(short[] initialData) {
        if (initialData == null) {
            throw new NullPointerException("initialData");
        }
        byte[] values = new byte[initialData.length * 2];
        for (int i = 0; i < initialData.length; i++) {
            int offset = i * 2;
            values[offset] = (byte) (initialData[i] >>> 8);
            values[offset + 1] = (byte) initialData[i];
        }
        return new ArrayByteBuffer(values);
    }

    /**
     * Allocates a float buffer initialized from float values.
     *
     * @param initialData the initial values
     * @return the allocated buffer
     */
    public FloatBuffer allocateFloatBuffer(float[] initialData) {
        if (initialData == null) {
            throw new NullPointerException("initialData");
        }
        return new ArrayFloatBuffer(initialData.clone());
    }

    /**
     * Allocates an int buffer initialized from int values.
     *
     * @param initialData the initial values
     * @return the allocated buffer
     */
    public IntBuffer allocateIntBuffer(int[] initialData) {
        if (initialData == null) {
            throw new NullPointerException("initialData");
        }
        return new ArrayIntBuffer(initialData.clone());
    }

    /**
     * Allocates a short buffer initialized from short values.
     *
     * @param initialData the initial values
     * @return the allocated buffer
     */
    public ShortBuffer allocateShortBuffer(short[] initialData) {
        if (initialData == null) {
            throw new NullPointerException("initialData");
        }
        return new ArrayShortBuffer(initialData.clone());
    }

    private static int checkedSize(int size) {
        if (size < 0) {
            throw new NegativeArraySizeException("size");
        }
        return size;
    }

    private static ArrayByteBuffer requireByteBuffer(ByteBuffer buff) {
        if (buff == null) {
            throw new NullPointerException("buff");
        }
        if (!(buff instanceof ArrayByteBuffer array)) {
            throw new IllegalArgumentException("buff");
        }
        return array;
    }

    private static ArrayShortBuffer requireShortBuffer(ShortBuffer buff) {
        if (buff == null) {
            throw new NullPointerException("buff");
        }
        if (!(buff instanceof ArrayShortBuffer array)) {
            throw new IllegalArgumentException("buff");
        }
        return array;
    }

    private static ArrayIntBuffer requireIntBuffer(IntBuffer buff) {
        if (buff == null) {
            throw new NullPointerException("buff");
        }
        if (!(buff instanceof ArrayIntBuffer array)) {
            throw new IllegalArgumentException("buff");
        }
        return array;
    }

    private static ArrayFloatBuffer requireFloatBuffer(FloatBuffer buff) {
        if (buff == null) {
            throw new NullPointerException("buff");
        }
        if (!(buff instanceof ArrayFloatBuffer array)) {
            throw new IllegalArgumentException("buff");
        }
        return array;
    }

    private abstract static class AbstractBuffer {
        int segmentOffset;
        int segmentLength;

        abstract int lengthValue();

        int segmentOffset() {
            return segmentOffset;
        }

        int segmentLength() {
            return segmentLength;
        }

        final void setSegmentInternal(int offset, int length) {
            if (offset < 0 || length <= 0 || offset + length > lengthValue()) {
                throw new ArrayIndexOutOfBoundsException();
            }
            segmentOffset = offset;
            segmentLength = length;
        }

        final void clearSegmentInternal() {
            segmentOffset = 0;
            segmentLength = lengthValue();
        }
    }

    private static final class ArrayByteBuffer extends AbstractBuffer implements ByteBuffer {
        private final byte[] values;

        ArrayByteBuffer(byte[] values) {
            this.values = values;
            clearSegmentInternal();
        }

        @Override
        public int length() {
            return values.length;
        }

        @Override
        int lengthValue() {
            return values.length;
        }

        @Override
        public void setSegment(int offset, int length) {
            setSegmentInternal(offset, length);
        }

        @Override
        public void clearSegment() {
            clearSegmentInternal();
        }

        @Override
        public byte[] get(int index, byte[] buff) {
            if (buff == null) {
                throw new NullPointerException("buff");
            }
            return get(index, buff, 0, buff.length);
        }

        @Override
        public byte[] get(int index, byte[] buff, int offset, int length) {
            checkRange(index, values.length, offset, buff == null ? -1 : buff.length, length);
            System.arraycopy(values, index, buff, offset, length);
            return buff;
        }

        @Override
        public void put(int index, byte[] buff) {
            if (buff == null) {
                throw new NullPointerException("buff");
            }
            put(index, buff, 0, buff.length);
        }

        @Override
        public void put(int index, byte[] buff, int offset, int length) {
            checkRange(index, values.length, offset, buff == null ? -1 : buff.length, length);
            System.arraycopy(buff, offset, values, index, length);
        }
    }

    private static final class ArrayShortBuffer extends AbstractBuffer implements ShortBuffer {
        private final short[] values;

        ArrayShortBuffer(short[] values) {
            this.values = values;
            clearSegmentInternal();
        }

        @Override
        public int length() {
            return values.length;
        }

        @Override
        int lengthValue() {
            return values.length;
        }

        @Override
        public void setSegment(int offset, int length) {
            setSegmentInternal(offset, length);
        }

        @Override
        public void clearSegment() {
            clearSegmentInternal();
        }

        @Override
        public short[] get(int index, short[] buff) {
            if (buff == null) {
                throw new NullPointerException("buff");
            }
            return get(index, buff, 0, buff.length);
        }

        @Override
        public short[] get(int index, short[] buff, int offset, int length) {
            checkRange(index, values.length, offset, buff == null ? -1 : buff.length, length);
            System.arraycopy(values, index, buff, offset, length);
            return buff;
        }

        @Override
        public void put(int index, short[] buff) {
            if (buff == null) {
                throw new NullPointerException("buff");
            }
            put(index, buff, 0, buff.length);
        }

        @Override
        public void put(int index, short[] buff, int offset, int length) {
            checkRange(index, values.length, offset, buff == null ? -1 : buff.length, length);
            System.arraycopy(buff, offset, values, index, length);
        }
    }

    private static final class ArrayIntBuffer extends AbstractBuffer implements IntBuffer {
        private final int[] values;

        ArrayIntBuffer(int[] values) {
            this.values = values;
            clearSegmentInternal();
        }

        @Override
        public int length() {
            return values.length;
        }

        @Override
        int lengthValue() {
            return values.length;
        }

        @Override
        public void setSegment(int offset, int length) {
            setSegmentInternal(offset, length);
        }

        @Override
        public void clearSegment() {
            clearSegmentInternal();
        }

        @Override
        public int[] get(int index, int[] buff) {
            if (buff == null) {
                throw new NullPointerException("buff");
            }
            return get(index, buff, 0, buff.length);
        }

        @Override
        public int[] get(int index, int[] buff, int offset, int length) {
            checkRange(index, values.length, offset, buff == null ? -1 : buff.length, length);
            System.arraycopy(values, index, buff, offset, length);
            return buff;
        }

        @Override
        public void put(int index, int[] buff) {
            if (buff == null) {
                throw new NullPointerException("buff");
            }
            put(index, buff, 0, buff.length);
        }

        @Override
        public void put(int index, int[] buff, int offset, int length) {
            checkRange(index, values.length, offset, buff == null ? -1 : buff.length, length);
            System.arraycopy(buff, offset, values, index, length);
        }
    }

    private static final class ArrayFloatBuffer extends AbstractBuffer implements FloatBuffer {
        private final float[] values;

        ArrayFloatBuffer(float[] values) {
            this.values = values;
            clearSegmentInternal();
        }

        @Override
        public int length() {
            return values.length;
        }

        @Override
        int lengthValue() {
            return values.length;
        }

        @Override
        public void setSegment(int offset, int length) {
            setSegmentInternal(offset, length);
        }

        @Override
        public void clearSegment() {
            clearSegmentInternal();
        }

        @Override
        public float[] get(int index, float[] buff) {
            if (buff == null) {
                throw new NullPointerException("buff");
            }
            return get(index, buff, 0, buff.length);
        }

        @Override
        public float[] get(int index, float[] buff, int offset, int length) {
            checkRange(index, values.length, offset, buff == null ? -1 : buff.length, length);
            System.arraycopy(values, index, buff, offset, length);
            return buff;
        }

        @Override
        public void put(int index, float[] buff) {
            if (buff == null) {
                throw new NullPointerException("buff");
            }
            put(index, buff, 0, buff.length);
        }

        @Override
        public void put(int index, float[] buff, int offset, int length) {
            checkRange(index, values.length, offset, buff == null ? -1 : buff.length, length);
            System.arraycopy(buff, offset, values, index, length);
        }

        @Override
        public FloatBuffer madd(FloatBuffer src1, FloatBuffer src2, float multiplier) {
            if (src1 == null && src2 == null) {
                throw new NullPointerException();
            }
            ArrayFloatBuffer left = src1 == null ? null : requireFloatBuffer(src1);
            ArrayFloatBuffer right = src2 == null ? null : requireFloatBuffer(src2);
            int expectedLength = segmentLength();
            if ((left != null && left.segmentLength() != expectedLength) || (right != null && right.segmentLength() != expectedLength)) {
                throw new IllegalArgumentException("segment");
            }
            int dst = segmentOffset();
            int leftIndex = left == null ? 0 : left.segmentOffset();
            int rightIndex = right == null ? 0 : right.segmentOffset();
            for (int i = 0; i < expectedLength; i++) {
                float value = 0f;
                if (left != null) {
                    value += left.values[leftIndex + i];
                }
                if (right != null) {
                    value += multiplier * right.values[rightIndex + i];
                }
                values[dst + i] = value;
            }
            return this;
        }

        @Override
        public FloatBuffer transform(FloatBuffer src, Matrix4f matrix, int itemSize, int itemCount) {
            ArrayFloatBuffer source = requireFloatBuffer(src);
            if (matrix == null) {
                throw new NullPointerException("matrix");
            }
            if (itemSize != 2 && itemSize != 3 && itemSize != 4) {
                throw new IllegalArgumentException("itemSize");
            }
            if (itemCount < 0) {
                throw new IllegalArgumentException("itemCount");
            }
            if (matrix.m == null) {
                throw new NullPointerException("matrix.m");
            }
            if (matrix.m.length < 16) {
                throw new ArrayIndexOutOfBoundsException("matrix.m");
            }
            int required = itemSize * itemCount;
            if (required > segmentLength() || required > source.segmentLength()) {
                throw new ArrayIndexOutOfBoundsException();
            }
            int dst = segmentOffset();
            int srcIndex = source.segmentOffset();
            float[] m = matrix.m;
            for (int item = 0; item < itemCount; item++) {
                float x = source.values[srcIndex++];
                float y = source.values[srcIndex++];
                if (itemSize == 2) {
                    values[dst++] = m[0] * x + m[4] * y + m[12];
                    values[dst++] = m[1] * x + m[5] * y + m[13];
                    continue;
                }
                float z = source.values[srcIndex++];
                if (itemSize == 3) {
                    values[dst++] = m[0] * x + m[4] * y + m[8] * z + m[12];
                    values[dst++] = m[1] * x + m[5] * y + m[9] * z + m[13];
                    values[dst++] = m[2] * x + m[6] * y + m[10] * z + m[14];
                    continue;
                }
                float w = source.values[srcIndex++];
                values[dst++] = m[0] * x + m[4] * y + m[8] * z + m[12] * w;
                values[dst++] = m[1] * x + m[5] * y + m[9] * z + m[13] * w;
                values[dst++] = m[2] * x + m[6] * y + m[10] * z + m[14] * w;
                values[dst++] = m[3] * x + m[7] * y + m[11] * z + m[15] * w;
            }
            return this;
        }
    }

    private static void checkRange(int index, int bufferLength, int offset, int arrayLength, int length) {
        if (index < 0 || offset < 0 || length < 0 || offset + length > arrayLength || index + length > bufferLength) {
            throw new IllegalArgumentException();
        }
    }
}
