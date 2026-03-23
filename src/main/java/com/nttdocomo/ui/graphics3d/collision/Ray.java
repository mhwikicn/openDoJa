package com.nttdocomo.ui.graphics3d.collision;

import com.nttdocomo.ui.util3d.Vector3D;

/**
 * Defines a ray shape.
 */
public class Ray extends Line {
    private final Vector3D start = new Vector3D();
    private final Vector3D direction = new Vector3D(0f, 0f, 1f);

    public Ray(Vector3D start, Vector3D direction) {
        super(start, new Vector3D(start));
        set(start, direction);
    }

    @Override
    public void set(Vector3D start, Vector3D direction) {
        if (start == null || direction == null) {
            throw new NullPointerException("vector");
        }
        this.start.set(start);
        this.direction.set(direction);
        Vector3D end = new Vector3D(start);
        end.add(direction);
        super.set(start, end);
    }

    public void setByPoints(Vector3D start, Vector3D end) {
        if (start == null || end == null) {
            throw new NullPointerException("vector");
        }
        Vector3D dir = new Vector3D(end);
        dir.add(-start.getX(), -start.getY(), -start.getZ());
        set(start, dir);
    }

    @Override
    public Vector3D getStartPosition(boolean transformed) {
        return transformed ? transformPoint(start) : new Vector3D(start);
    }

    @Override
    public Vector3D getEndPosition(boolean transformed) {
        Vector3D end = new Vector3D(start);
        end.add(direction);
        return transformed ? transformPoint(end) : end;
    }

    public Vector3D getDirection(boolean transformed) {
        return transformed ? transformDirection(direction) : new Vector3D(direction);
    }

    @Override
    public void createMesh(int slice, int stack, float scale) {
        super.createMesh(slice, stack, scale);
    }
}
