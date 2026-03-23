package com.nttdocomo.ui.sound3d;

import com.nttdocomo.ui.Audio3DLocalization;
import com.nttdocomo.ui.util3d.Vector3D;

/**
 * Represents a sound source in polar coordinates.
 */
public class PolarPosition implements SoundPosition, Audio3DLocalization {
    private static float defaultCoordinateFactor = 1f;

    private final float coordinateFactor;
    private final Vector3D velocity = new Vector3D();
    private float distance;
    private float direction;
    private float elevation;

    /**
     * Creates a position object using the default coordinate factor.
     */
    public PolarPosition() {
        this(defaultCoordinateFactor);
    }

    /**
     * Creates a position object.
     *
     * @param coordinateFactor the coordinate factor
     */
    public PolarPosition(float coordinateFactor) {
        this.coordinateFactor = coordinateFactor;
    }

    /**
     * Sets the default coordinate factor.
     *
     * @param factor the factor to use for newly created instances
     */
    public static void setDefaultCoordinateFactor(float factor) {
        defaultCoordinateFactor = factor;
    }

    /**
     * Returns the coordinate factor.
     *
     * @return the coordinate factor
     */
    public float getCoordinateFactor() {
        return coordinateFactor;
    }

    /**
     * Sets the distance component.
     *
     * @param distance the distance to set
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }

    /**
     * Returns the distance component.
     *
     * @return the distance
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Sets the direction component.
     *
     * @param direction the direction to set
     */
    public void setDirection(float direction) {
        this.direction = direction;
    }

    /**
     * Returns the direction component.
     *
     * @return the direction
     */
    public float getDirection() {
        return direction;
    }

    /**
     * Sets the elevation component.
     *
     * @param elevation the elevation to set
     */
    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    /**
     * Returns the elevation component.
     *
     * @return the elevation
     */
    public float getElevation() {
        return elevation;
    }

    /**
     * Sets the polar position from a Cartesian vector.
     *
     * @param position the Cartesian position
     */
    public void setPosition(Vector3D position) {
        if (position == null) {
            throw new NullPointerException("position");
        }
        float x = position.getX();
        float y = position.getY();
        float z = position.getZ();
        float len = (float) java.lang.Math.sqrt(x * x + y * y + z * z);
        distance = coordinateFactor == 0f ? len : len / coordinateFactor;
        direction = (float) java.lang.Math.atan2(x, z);
        elevation = len == 0f ? 0f : (float) java.lang.Math.asin(y / len);
    }

    /**
     * Returns the current velocity.
     *
     * @return the velocity
     */
    public Vector3D getVelocity() {
        return new Vector3D(velocity);
    }

    /**
     * Sets the velocity.
     *
     * @param velocity the velocity to set
     */
    public void setVelocity(Vector3D velocity) {
        if (velocity == null) {
            throw new NullPointerException("velocity");
        }
        this.velocity.set(velocity);
    }
}
