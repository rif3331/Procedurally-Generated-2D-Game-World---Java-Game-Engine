package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

/**
 * Represents a tree in the game world.
 * Trees have varying heights and are composed of blocks.
 */
public class Tree extends GameObject {

    /**
     * The width of the tree trunk in pixels.
     */
    public static final int WIDTH_TREE = Block.SIZE;

    /**
     * Tag used to identify tree blocks in the game world.
     */
    public static final String Tree_TAG = "tree";

    private static final Color BASE_TREE_COLOR = new Color(100, 50, 20);
    private static final int TREE_HEIGHT_VARIATION = 151;
    private static final int TREE_HEIGHT_MIN = 100;
    private static Random random;

    /**
     * Constructs a Tree instance.
     *
     * @param topLeftCorner The position of the top-left corner of the tree.
     */
    public Tree(Vector2 topLeftCorner) {
        super(topLeftCorner, new Vector2(WIDTH_TREE, calculateTreeHeight(topLeftCorner.x())),
                new RectangleRenderable(BASE_TREE_COLOR));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        random = new Random(PepseGameManager.SEED);
        setTag(Tree_TAG);
    }

    /**
     * Initializes the random generator with a specific seed.
     *
     * @param seed The seed for the random generator.
     */
    public static void initializeRandom(long seed) {
        random = new Random(seed);
    }

    /**
     * Calculates the height of the tree based on its x-position.
     *
     * @param xPosition The x-coordinate of the tree.
     * @return The height of the tree.
     */
    public static int calculateTreeHeight(float xPosition) {
        random.setSeed((long) xPosition);
        return random.nextInt(TREE_HEIGHT_VARIATION) + TREE_HEIGHT_MIN;
    }
}
