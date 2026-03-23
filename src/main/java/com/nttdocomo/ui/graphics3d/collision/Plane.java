package com.nttdocomo.ui.graphics3d.collision;

import com.nttdocomo.ui.util3d.Vector3D;

/**
 * Defines an infinite plane.
 */
public class Plane extends AbstractShape {
    private final Vector3D position = new Vector3D();
    private final Vector3D normal = new Vector3D(0f, 1f, 0f);
    private boolean hittingFromBackFaceEnabled;

    public Plane(Vector3D position, Vector3D normal) {
        super(TYPE_PLANE);
        set(position, normal);
    }

    public Plane(Vector3D p0, Vector3D p1, Vector3D p2) {
        super(TYPE_PLANE);
        set(p0, p1, p2);
    }

    public void set(Vector3D position, Vector3D normal) {
        if (position == null || normal == null) {
            throw new NullPointerException("vector");
        }
        this.position.set(position);
        this.normal.set(normal);
        this.normal.normalize();
    }

    public void set(Vector3D p0, Vector3D p1, Vector3D p2) {
        if (p0 == null || p1 == null || p2 == null) {
            throw new NullPointerException("vector");
        }
        this.position.set(p0);
        Vector3D ab = new Vector3D(p1);
        ab.add(-p0.getX(), -p0.getY(), -p0.getZ());
        Vector3D ac = new Vector3D(p2);
        ac.add(-p0.getX(), -p0.getY(), -p0.getZ());
        this.normal.cross(ab, ac);
        this.normal.normalize();
    }

    public Vector3D getPosition(boolean transformed) {
        return transformed ? transformPoint(position) : new Vector3D(position);
    }

    public Vector3D getNormal(boolean transformed) {
        Vector3D out = transformed ? transformDirection(normal) : new Vector3D(normal);
        out.normalize();
        return out;
    }

    public void setHittingFromBackFaceEnabled(boolean enabled) {
        hittingFromBackFaceEnabled = enabled;
    }

    public boolean isHittingFromBackFaceEnabled() {
        return hittingFromBackFaceEnabled;
    }

    @Override
    public void createMesh(int slice, int stack, float scale) {
        super.createMesh(slice, stack, scale);
    }
}
