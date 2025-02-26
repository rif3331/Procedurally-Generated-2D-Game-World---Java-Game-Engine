package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents rain in the game world.
 * This class handles the creation and disappearance of rain blocks triggered by specific events.
 */
public class Rain implements JumpListener {

    private static final Color BASE_RAIN_COLOR = new Color(12, 174, 203);
    private static final int GRAVITY = 20;
    private static final int RAIN_SIZE = 10;
    private static final int NUM_RAINS = 3;
    private static final float DISAPPEAR_TRANSITION_TIME = 3f;
    private static final float DISAPPEAR_INITIAL_VALUE = 100f;
    private static final float DISAPPEAR_FINAL_VALUE = 0f;
    private static final float FULL_OPACITY = 1f;
    private static final int SPACE = 15;

    private final List<Block> rainBlocks;
    private final GameObjectCollection gameObjects;

    /**
     * Constructs a Rain instance.
     *
     * @param gameObjects The collection of game objects where rain blocks will be added.
     */
    public Rain(GameObjectCollection gameObjects) {
        this.rainBlocks = new ArrayList<>();
        this.gameObjects = gameObjects;
    }

    /**
     * Creates rain blocks at the given cloud position.
     *
     * @param cloudPosition The position of the cloud that triggers the rain.
     * @return A list of rain blocks created at the specified position.
     */
    public List<Block> create(Vector2 cloudPosition) {
        for (int i = 0; i < NUM_RAINS; i++) {
            int rainDropX = (int) (cloudPosition.x() + i * (RAIN_SIZE + SPACE));
            int rainDropY = (int) cloudPosition.y() + RAIN_SIZE;
            Vector2 position = new Vector2(rainDropX, rainDropY);
            Block rainDrop = new Block(position,
                    new OvalRenderable(BASE_RAIN_COLOR));
            rainDrop.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
            rainDrop.transform().setAccelerationY(GRAVITY);
            rainBlocks.add(rainDrop);
            gameObjects.addGameObject(rainDrop, Layer.STATIC_OBJECTS);
        }
        this.disappear();
        return rainBlocks;
    }

    /**
     * Triggers the disappearance animation for all rain blocks.
     * The blocks gradually become transparent over time.
     */
    public void disappear() {
        for (Block block : rainBlocks) {
            if (block.renderer().getOpaqueness() == FULL_OPACITY) {
                new Transition<>(
                        block, opaqueness -> block.renderer().setOpaqueness(opaqueness
                        / DISAPPEAR_INITIAL_VALUE),
                        DISAPPEAR_INITIAL_VALUE,
                        DISAPPEAR_FINAL_VALUE, Transition.LINEAR_INTERPOLATOR_FLOAT,
                        DISAPPEAR_TRANSITION_TIME,
                        Transition.TransitionType.TRANSITION_ONCE, null
                );
            }
        }
    }

    /**
     * Handles jump events to create rain.
     *
     * @param cloudPosition The position of the cloud triggering the rain.
     */
    @Override
    public void onJump(Vector2 cloudPosition) {
        this.create(cloudPosition);
    }
}
