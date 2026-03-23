package com.nttdocomo.opt.ui;

/**
 * Defines a set of optional sprites.
 */
public class SpriteSet {
    private final Sprite[] sprites;
    private int collisionMask;

    /**
     * Creates a sprite set.
     *
     * @param sprites the sprites to hold
     */
    public SpriteSet(Sprite[] sprites) {
        this.sprites = sprites == null ? new Sprite[0] : sprites.clone();
    }

    /**
     * Returns the number of sprites.
     *
     * @return the number of sprites
     */
    public int getCount() {
        return sprites.length;
    }

    /**
     * Returns a copy of the sprite array.
     *
     * @return the sprites in this set
     */
    public Sprite[] getSprites() {
        return sprites.clone();
    }

    /**
     * Returns the sprite at the specified index.
     *
     * @param index the index to query
     * @return the sprite
     */
    public Sprite getSprite(int index) {
        return sprites[index];
    }

    /**
     * Marks all sprites as collision targets.
     */
    public void setCollisionAll() {
        collisionMask = -1;
    }

    /**
     * Marks the specified sprite as a collision target.
     *
     * @param index the sprite index
     */
    public void setCollisionOf(int index) {
        collisionMask |= 1 << index;
    }

    /**
     * Returns whether two sprites collide.
     *
     * @param leftIndex the first sprite index
     * @param rightIndex the second sprite index
     * @return {@code true} if the sprites overlap
     */
    public boolean isCollision(int leftIndex, int rightIndex) {
        Sprite left = sprites[leftIndex];
        Sprite right = sprites[rightIndex];
        if (left == null || right == null || !left.isVisible() || !right.isVisible()) {
            return false;
        }
        return left.getX() < right.getX() + right.getWidth()
                && left.getX() + left.getWidth() > right.getX()
                && left.getY() < right.getY() + right.getHeight()
                && left.getY() + left.getHeight() > right.getY();
    }

    /**
     * Returns the collision flag mask.
     *
     * @param index the sprite index
     * @return the collision mask
     */
    public int getCollisionFlag(int index) {
        return collisionMask;
    }
}
