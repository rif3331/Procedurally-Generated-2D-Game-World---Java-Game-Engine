package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Represents the terrain in the game world.
 * The terrain is procedurally generated and consists of blocks forming the ground.
 */
public class Terrain {

    /**
     * Tag used to identify ground blocks in the game world.
     */
    public static final String GROUND_TAG = "ground";

    private static final float INITIAL_OF_GROUND_HEIGHT = 2f / 3f;
    private static final int TERRAIN_DEPTH = 20;
    private static final int NOISE_FACTOR = 55;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private final NoiseGenerator genarator1;
    private final float groundHeightAtX0;
    private final Random random;
    private List<Block> blocks;

    /**
     * Constructs a Terrain instance.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param seed The seed for generating procedural noise.
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        this.random = new Random(seed);
        this.blocks = new ArrayList<>();
        this.groundHeightAtX0 = (windowDimensions.y() * INITIAL_OF_GROUND_HEIGHT);
        int tempGroundHeightAtX0 = (int) groundHeightAtX0;
        genarator1 = new NoiseGenerator(seed, tempGroundHeightAtX0);
    }

    /**
     * Calculates the ground height at a specific x-coordinate.
     *
     * @param x The x-coordinate to calculate the ground height.
     * @return The ground height at the specified x-coordinate.
     */
    public float groundHeightAt(float x) {
        random.setSeed((long) x);
        return groundHeightAtX0 + (float) genarator1.noise(x, NOISE_FACTOR);
    }

    /**
     * Filters and removes blocks that are outside the specified range.
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A list of removed blocks that were outside the range.
     */
    public List<Block> filterBlocksInRange(int minX, int maxX) {
        List<Block> removedBlocks = new ArrayList<>();
        Iterator<Block> iterator = blocks.iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            int x = (int) block.getTopLeftCorner().x();
            if (x < minX || x > maxX) {
                removedBlocks.add(block);
                iterator.remove();
            }
        }
        return removedBlocks;
    }

    /**
     * Creates blocks within the specified range.
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A list of blocks created within the specified range.
     */
    public List<Block> createInRange(int minX, int maxX) {
        this.blocks = new ArrayList<>();
        for (int x = Math.floorDiv(minX, Block.SIZE) * Block.SIZE; x <= maxX; x += Block.SIZE) {
            for (int y = (int) groundHeightAt(x);
                 y < (Block.SIZE * TERRAIN_DEPTH) + (int) groundHeightAt(x); y += Block.SIZE) {
                Block block = new Block(new Vector2(x, y),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                block.setTag(GROUND_TAG);
                blocks.add(block);
            }
        }
        return blocks;
    }

}
