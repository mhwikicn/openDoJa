package com.nttdocomo.ui.graphics3d.collision;

import com.nttdocomo.ui.util3d.Transform;

/**
 * Defines an axis-aligned capsule.
 */
public class AABCapsule extends Capsule implements AxisAlignedBV {
    public AABCapsule(float radius, float height) {
        super(radius, height);
    }

    public float getRadius(boolean transformed) {
        return transformed ? getRadius() * getScale() : getRadius();
    }

    public float getHeight(boolean transformed) {
        return transformed ? getHeight() * getScale() : getHeight();
    }

    @Override
    public Transform getMeshTransform(Transform transform) {
        return super.getMeshTransform(transform);
    }

    @Override
    public float getEffectiveRadius(com.nttdocomo.ui.util3d.Vector3D direction) {
        return super.getEffectiveRadius(direction);
    }

    @Override
    public void createMesh(int slice, int stack, float scale) {
        super.createMesh(slice, stack, scale);
    }

    @Override
    public com.nttdocomo.ui.graphics3d.Primitive getMesh() {
        return super.getMesh();
    }
}
