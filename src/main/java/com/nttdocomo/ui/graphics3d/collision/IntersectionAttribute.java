package com.nttdocomo.ui.graphics3d.collision;

import com.nttdocomo.ui.util3d.Vector3D;

/**
 * Holds information about an intersection point.
 */
public class IntersectionAttribute {
    public float distance;
    public float[] textureUV;
    public Vector3D normal;
    public int colorRGBA;
    public int blendMode;
}
