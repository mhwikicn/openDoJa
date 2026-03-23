package com.nttdocomo.ui.graphics3d.collision;

import com.nttdocomo.ui.util3d.Transform;
import com.nttdocomo.ui.util3d.Vector3D;

/**
 * Base implementation of {@link BoundingVolume}.
 */
public abstract class AbstractBV extends AbstractShape implements BoundingVolume {
    private final Vector3D center = new Vector3D();
    private int rotate;
    private boolean hittingFromBackFaceEnabled;

    protected AbstractBV(int shapeType) {
        super(shapeType);
    }

    @Override
    public final void setCenter(Vector3D center) {
        if (center == null) {
            throw new NullPointerException("center");
        }
        this.center.set(center);
    }

    @Override
    public final Vector3D getCenter(boolean transformed) {
        return transformed ? transformPoint(center) : new Vector3D(center);
    }

    @Override
    public final void setRotate(int rotate) {
        if (rotate < ROTATE_NONE || rotate > ROTATE_YX) {
            throw new IllegalArgumentException("rotate");
        }
        this.rotate = rotate;
    }

    @Override
    public final int getRotate() {
        return rotate;
    }

    @Override
    public Transform getMeshTransform(Transform destination) {
        Transform out = super.getMeshTransform(destination);
        out.translate(center);
        return out;
    }

    @Override
    public final void setHittingFromBackFaceEnabled(boolean enabled) {
        hittingFromBackFaceEnabled = enabled;
    }

    @Override
    public final boolean isHittingFromBackFaceEnabled() {
        return hittingFromBackFaceEnabled;
    }

    protected Vector3D axisVector() {
        return switch (rotate) {
            case ROTATE_X -> new Vector3D(1f, 0f, 0f);
            case ROTATE_Z -> new Vector3D(0f, 0f, 1f);
            default -> new Vector3D(0f, 1f, 0f);
        };
    }
}
