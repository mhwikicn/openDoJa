package com.nttdocomo.opt.ui;

import com.nttdocomo.ui.Graphics;
import com.nttdocomo.ui.Image;

/**
 * Defines an optional sprite object.
 */
public class Sprite {
    private Image image;
    private int sourceX;
    private int sourceY;
    private int width;
    private int height;
    private int x;
    private int y;
    private boolean visible = true;
    private int flipMode = Graphics.FLIP_NONE;
    private int renderOperator = Graphics2.OP_REPL;
    private int srcRatio = 255;
    private int dstRatio = 255;

    /**
     * Applications cannot create this object directly.
     */
    protected Sprite() {
    }

    /**
     * Creates a sprite from an entire image.
     *
     * @param image the sprite image
     */
    public Sprite(Image image) {
        this(image, 0, 0, image == null ? 0 : image.getWidth(), image == null ? 0 : image.getHeight());
    }

    /**
     * Creates a sprite from an image region.
     *
     * @param image the sprite image
     * @param x the source x coordinate
     * @param y the source y coordinate
     * @param width the region width
     * @param height the region height
     */
    public Sprite(Image image, int x, int y, int width, int height) {
        setImage(image, x, y, width, height);
    }

    /**
     * Sets the sprite location.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the sprite image.
     *
     * @param image the image
     */
    public void setImage(Image image) {
        setImage(image, 0, 0, image == null ? 0 : image.getWidth(), image == null ? 0 : image.getHeight());
    }

    /**
     * Sets the sprite image region.
     *
     * @param image the image
     * @param x the source x coordinate
     * @param y the source y coordinate
     * @param width the region width
     * @param height the region height
     */
    public void setImage(Image image, int x, int y, int width, int height) {
        this.image = image;
        this.sourceX = x;
        this.sourceY = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the visibility flag.
     *
     * @param visible {@code true} if the sprite should be visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Returns whether the sprite is visible.
     *
     * @return {@code true} if visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the flip mode.
     *
     * @param flipMode the flip mode
     */
    public void setFlipMode(int flipMode) {
        this.flipMode = flipMode;
    }

    /**
     * Sets the render mode.
     *
     * @param operator the render operator
     * @param srcRatio the source ratio
     * @param dstRatio the destination ratio
     */
    public void setRenderMode(int operator, int srcRatio, int dstRatio) {
        this.renderOperator = operator;
        this.srcRatio = srcRatio;
        this.dstRatio = dstRatio;
    }

    /**
     * Returns the x coordinate.
     *
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate.
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the width.
     *
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height.
     *
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    Image image() {
        return image;
    }

    int sourceX() {
        return sourceX;
    }

    int sourceY() {
        return sourceY;
    }

    int flipMode() {
        return flipMode;
    }

    int renderOperator() {
        return renderOperator;
    }

    int srcRatio() {
        return srcRatio;
    }

    int dstRatio() {
        return dstRatio;
    }
}
