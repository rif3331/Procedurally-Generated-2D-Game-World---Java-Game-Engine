package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a single block in the game world.
 * Blocks are the fundamental building units of the terrain.
 */
public class Block extends GameObject {

    /**
     * The size of a block in pixels.
     */
    public static final int SIZE = 30;

    /**
     * Constructs a new Block instance.
     *
     * @param topLeftCorner The position of the top-left corner of the block.
     * @param renderable The renderable to display for the block.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

}
