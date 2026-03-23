package com.nttdocomo.opt.ui.j3d;

import opendoja.g3d.FixedPoint;

/**
 * Defines a class that represents a three-dimensional fixed-point vector.
 * Each component is handled as a fixed-point number that maps {@code 1.0} to
 * {@code 4096}. Therefore, the length of a vector after
 * {@link #normalize()} is {@code 4096}.
 *
 * <p>If calculation overflows the {@code int} range, the result is
 * device-dependent.</p>
 *
 * <p>Introduced in DoJa-2.0.</p>
 */
public class Vector3D {
    /**
     * The x component of the vector.
     */
    public int x;

    /**
     * The y component of the vector.
     */
    public int y;

    /**
     * The z component of the vector.
     */
    public int z;

    /**
     * Creates a vector object.
     * All components are initialized to {@code 0}.
     */
    public Vector3D() {
    }

    /**
     * Creates a vector object with the specified components.
     *
     * @param x the x component of the vector
     * @param y the y component of the vector
     * @param z the z component of the vector
     */
    public Vector3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Normalizes this vector.
     * The length of the normalized vector is {@code 4096}.
     *
     */
    public void normalize() {
        // Map-space coordinates can be much larger than UI-scene coordinates. Keep the length
        // accumulation in 64-bit so camera/lookAt setup does not overflow before normalization.
        long lengthSquared = (long) x * (long) x + (long) y * (long) y + (long) z * (long) z;
        int length = (int) java.lang.Math.round(java.lang.Math.sqrt(lengthSquared));
        if (length == 0) {
            x = 0;
            y = 0;
            z = FixedPoint.ONE;
            return;
        }
        x = (int) ((((long) x) << 12) / length);
        y = (int) ((((long) y) << 12) / length);
        z = (int) ((((long) z) << 12) / length);
    }

    /**
     * Calculates the inner product of this vector and the specified vector.
     * This is the same as calling {@link #dot(Vector3D, Vector3D)} with this
     * object and {@code other}.
     *
     * @param other the vector with which to take the inner product
     * @return the inner-product value
     * @throws NullPointerException if {@code other} is {@code null}
     */
    public int dot(Vector3D other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Calculates the inner product of the two specified vectors.
     *
     * @param left one of the vectors
     * @param right the other vector
     * @return the inner-product value of {@code left} and {@code right}
     * @throws NullPointerException if {@code left} or {@code right} is
     *         {@code null}
     */
    public static int dot(Vector3D left, Vector3D right) {
        return left.dot(right);
    }

    /**
     * Calculates the cross product of this vector and the specified vector.
     * This is the same as calling {@link #cross(Vector3D, Vector3D)} with this
     * object and {@code other}.
     *
     * @param other the vector with which to take the cross product
     * @throws NullPointerException if {@code other} is {@code null}
     */
    public void cross(Vector3D other) {
        cross(this, other);
    }

    /**
     * Calculates the cross product of the two specified vectors.
     * The result of {@code left x right} is stored in this object.
     *
     * @param left one of the vectors
     * @param right the other vector
     * @throws NullPointerException if {@code left} or {@code right} is
     *         {@code null}
     */
    public void cross(Vector3D left, Vector3D right) {
        int nextX = left.y * right.z - left.z * right.y;
        int nextY = left.z * right.x - left.x * right.z;
        int nextZ = left.x * right.y - left.y * right.x;
        x = nextX;
        y = nextY;
        z = nextZ;
    }
}
