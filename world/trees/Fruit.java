package pepse.world.trees;

import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;

/**
 * Represents a fruit in the game world.
 * Fruits can regenerate after being consumed or removed.
 */
public class Fruit extends Block {

    /**
     * Tag used to identify fruit blocks in the game world.
     */
    public static final String FRUIT_TAG = "fruit";

    private static final int CYCLE_OF_FRUIT = 30;
    private final Vector2 initialPosition;
    private final Renderable renderable;

    /**
     * Constructs a Fruit instance.
     *
     * @param topLeftCorner The position of the top-left corner of the fruit.
     */
    public Fruit(Vector2 topLeftCorner) {
        super(topLeftCorner, new OvalRenderable(Color.RED));
        this.initialPosition = topLeftCorner;
        this.renderable = this.renderer().getRenderable();
        physics().preventIntersectionsFromDirection(null);
        physics().setMass(1);
    }

    /**
     * Restarts the fruit by hiding it temporarily and then resetting its position and appearance.
     */
    public void restart() {
        this.renderer().setRenderable(null);
        this.transform().setTopLeftCorner(Vector2.ZERO);
        new ScheduledTask(this, CYCLE_OF_FRUIT, false, () -> {
            this.renderer().setRenderable(renderable);
            this.transform().setTopLeftCorner(initialPosition);
        });
    }

    /**
     * Gets the initial position of the fruit.
     *
     * @return The initial position of the fruit.
     */
    public Vector2 getInitialPosition() {
        return initialPosition;
    }
}