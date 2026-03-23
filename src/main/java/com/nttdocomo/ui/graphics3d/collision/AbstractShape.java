package com.nttdocomo.ui.graphics3d.collision;

import com.nttdocomo.ui.graphics3d.Primitive;
import com.nttdocomo.ui.util3d.Transform;
import com.nttdocomo.ui.util3d.Vector3D;

/**
 * Base implementation of {@link Shape}.
 */
public abstract class AbstractShape implements Shape {
    private final int shapeType;
    private final Transform transform = new Transform();
    private Object attribute;
    private Primitive mesh;
    protected float meshScaleX = 1f;
    protected float meshScaleY = 1f;
    protected float meshScaleZ = 1f;

    protected AbstractShape(int shapeType) {
        this.shapeType = shapeType;
    }

    @Override
    public final int getShapeType() {
        return shapeType;
    }

    @Override
    public Primitive getMesh() {
        return mesh;
    }

    @Override
    public Transform getMeshTransform(Transform destination) {
        return getTransform(TRANS_SHAPE_WORLD, destination);
    }

    @Override
    public final void deleteMesh() {
        mesh = null;
    }

    @Override
    public final void setTransform(Transform transform) {
        if (transform == null) {
            throw new NullPointerException("transform");
        }
        this.transform.set(transform);
    }

    @Override
    public Transform getTransform(int type, Transform destination) {
        Transform out = destination == null ? new Transform() : destination;
        out.set(transform);
        return out;
    }

    @Override
    public float getScale() {
        float sx = scaleOfColumn(0, 1, 2);
        float sy = scaleOfColumn(4, 5, 6);
        float sz = scaleOfColumn(8, 9, 10);
        return (sx + sy + sz) / 3f;
    }

    @Override
    public final void setAttribute(Object attribute) {
        this.attribute = attribute;
    }

    @Override
    public final Object getAttribute() {
        return attribute;
    }

    @Override
    public void createMesh(int slice, int stack, float scale) {
        meshScaleX = scale;
        meshScaleY = scale;
        meshScaleZ = scale;
        mesh = createMeshBody();
    }

    protected Primitive createMeshBody() {
        return new Primitive(Primitive.PRIMITIVE_POINTS, Primitive.NORMAL_NONE, 0);
    }

    protected Transform baseTransform() {
        return transform;
    }

    protected Vector3D transformPoint(Vector3D point) {
        Vector3D out = new Vector3D();
        transform.transVector(point, out);
        return out;
    }

    protected Vector3D transformDirection(Vector3D direction) {
        Vector3D out = new Vector3D(
                baseTransform().get(0) * direction.getX() + baseTransform().get(1) * direction.getY() + baseTransform().get(2) * direction.getZ(),
                baseTransform().get(4) * direction.getX() + baseTransform().get(5) * direction.getY() + baseTransform().get(6) * direction.getZ(),
                baseTransform().get(8) * direction.getX() + baseTransform().get(9) * direction.getY() + baseTransform().get(10) * direction.getZ()
        );
        return out;
    }

    private float scaleOfColumn(int a, int b, int c) {
        float x = transform.get(a);
        float y = transform.get(b);
        float z = transform.get(c);
        return (float) java.lang.Math.sqrt(x * x + y * y + z * z);
    }
}
