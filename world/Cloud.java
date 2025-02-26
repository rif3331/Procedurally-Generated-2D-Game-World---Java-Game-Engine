package pepse.world;

import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents clouds in the game world.
 * Clouds are made up of blocks and have animations for movement.
 */
public class Cloud {
    private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255);
    private static final float CLOUD_HEIGHT = 1;
    private static final String CLOUD_TAG = "cloud";
    private static final float MOVE_BLOCK_INITIAL_VALUE = 0f;
    private static final int MOVE_BLOCK_TRANSITION_TIME = 30;
    private static final float WINDOW_CENTER_RATIO = 2f;
    private static final List<List<Integer>> CLOUD_SHAPE = List.of(
            List.of(0, 1, 1, 0, 0, 0),
            List.of(1, 1, 1, 0, 1, 0),
            List.of(1, 1, 1, 1, 1, 1),
            List.of(1, 1, 1, 1, 1, 1),
            List.of(0, 1, 1, 1, 0, 0),
            List.of(0, 0, 0, 0, 0, 0)
    );

    private final Vector2 windowDimensions;
    private final List<Block> cloudBlocks;

    /**
     * Constructs a Cloud instance.
     *
     * @param windowDimensions Dimensions of the game window.
     */
    public Cloud(Vector2 windowDimensions) {
        this.windowDimensions = windowDimensions;
        this.cloudBlocks = new ArrayList<>();
    }

    /**
     * Animates a cloud block by moving it horizontally in a loop.
     *
     * @param block The block to animate.
     * @param initialPos The initial position of the block.
     */
    private void moveBlock(Block block, Vector2 initialPos) {
        new Transition<>(block,
                (Float x) -> block.setTopLeftCorner(new Vector2(initialPos.x() + x, initialPos.y())),
                MOVE_BLOCK_INITIAL_VALUE, windowDimensions.x(),
                Transition.LINEAR_INTERPOLATOR_FLOAT, MOVE_BLOCK_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_LOOP, null
        );
    }

    /**
     * Creates cloud blocks within a specified range.
     *
     * @param startX The starting x-coordinate.
     * @param length The length of the range to create clouds.
     * @return A list of cloud blocks created in the specified range.
     */
    public List<Block> createInRange(int startX, int length) {
        int endX = startX + length;
        Vector2 basePos = new Vector2(startX, CLOUD_HEIGHT * Block.SIZE);
        for (int x = startX, i = 0; x <= endX && i < CLOUD_SHAPE.get(0).size(); x += Block.SIZE, i++) {
            for (int j = 0; j < CLOUD_SHAPE.size(); j++) {
                if (CLOUD_SHAPE.get(j).get(i) == 1) {
                    Vector2 relative = new Vector2(x, j * Block.SIZE);
                    Vector2 initialPos = basePos.add(relative);
                    Block block = new Block(initialPos,
                            new RectangleRenderable(ColorSupplier.approximateMonoColor(BASE_CLOUD_COLOR)));
                    block.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
                    block.setTag(CLOUD_TAG);
                    cloudBlocks.add(block);
                    moveBlock(block, initialPos);
                }
            }
        }
        return cloudBlocks;
    }

    /**
     * Retrieves the position of the cloud based on its blocks.
     *
     * @return The position of the cloud.
     */
    public Vector2 getCloudPosition() {
        if (cloudBlocks.isEmpty()) {
            return Vector2.ZERO;
        }
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float bottomY = Float.MIN_VALUE;
        for (Block block : cloudBlocks) {
            Vector2 blockPos = block.getTopLeftCorner();
            if (blockPos.y() > bottomY) {
                bottomY = blockPos.y();
            }
        }
        for (Block block : cloudBlocks) {
            Vector2 blockPos = block.getTopLeftCorner();
            if (blockPos.y() == bottomY) {
                minX = Math.min(minX, blockPos.x());
                maxX = Math.max(maxX, blockPos.x());
            }
        }
        float centerX = (minX + maxX) / WINDOW_CENTER_RATIO;
        return new Vector2(centerX, bottomY + Block.SIZE);
    }
}
