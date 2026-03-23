package com.nttdocomo.ui.graphics3d.collision;

import com.nttdocomo.ui.util3d.Transform;
import com.nttdocomo.ui.util3d.Vector3D;

/**
 * Defines an axis-aligned box.
 */
public class AABBox extends Box implements AxisAlignedBV {
    public AABBox(Vector3D size) {
        super(size);
    }

    public Vector3D getSize(boolean transformed) {
        Vector3D size = getSize();
        if (!transformed) {
            return size;
        }
        float scale = getScale();
        return new Vector3D(size.getX() * scale, size.getY() * scale, size.getZ() * scale);
    }

    @Override
    public Transform getMeshTransform(Transform transform) {
        return super.getMeshTransform(transform);
    }

    @Override
    public float getEffectiveRadius(Vector3D direction) {
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
