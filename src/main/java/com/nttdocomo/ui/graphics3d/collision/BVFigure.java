package com.nttdocomo.ui.graphics3d.collision;

import com.nttdocomo.ui.graphics3d.ActionTable;
import com.nttdocomo.ui.util3d.Transform;

import java.util.HashMap;
import java.util.Map;

/**
 * Associates bounding volumes with a figure or its bones.
 */
public class BVFigure {
    public static final int ID_WHOLE_FIGURE = -1;
    public static final int ID_NOT_FIGURE = -2;

    private final Transform transform = new Transform();
    private final Map<Integer, BoundingVolume> volumes = new HashMap<>();
    private final Map<Integer, Boolean> hittingEnabled = new HashMap<>();
    private final int numBones;
    private ActionTable actionTable;
    private int action;
    private int time;

    BVFigure() {
        this(0);
    }

    BVFigure(int numBones) {
        this.numBones = java.lang.Math.max(0, numBones);
    }

    public void setTransform(Transform transform) {
        if (transform == null) {
            throw new NullPointerException("transform");
        }
        this.transform.set(transform);
    }

    public Transform getTransform(Transform destination) {
        Transform out = destination == null ? new Transform() : destination;
        out.set(transform);
        return out;
    }

    public void setAction(ActionTable actionTable, int action) {
        this.actionTable = actionTable;
        this.action = action;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getNumBones() {
        return numBones;
    }

    public void setBV(BoundingVolume volume) {
        setBV(volume, ID_WHOLE_FIGURE);
    }

    public void setBV(BoundingVolume volume, int boneId) {
        if (volume == null) {
            volumes.remove(boneId);
        } else {
            volumes.put(boneId, volume);
        }
    }

    public BoundingVolume getBV() {
        return getBV(ID_WHOLE_FIGURE);
    }

    public BoundingVolume getBV(int boneId) {
        return volumes.get(boneId);
    }

    public void calculateBV(int type, float expand) {
        if (!volumes.containsKey(ID_WHOLE_FIGURE)) {
            volumes.put(ID_WHOLE_FIGURE, BVBuilder.createShape(type, expand));
        }
    }

    public void setHittingEnabled(int boneId, boolean enabled) {
        hittingEnabled.put(boneId, enabled);
    }

    public boolean isHittingEnabled(int boneId) {
        return hittingEnabled.getOrDefault(boneId, true);
    }

    Map<Integer, BoundingVolume> volumes() {
        return volumes;
    }
}
