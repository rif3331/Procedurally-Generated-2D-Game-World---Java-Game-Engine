package pepse.world.trees;

import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;

/**
 * Represents a leaf in the game world.
 * Leaves can oscillate and change their dimensions over time to simulate natural movement.
 */
public class Leaf extends Block {

    /**
     * Tag used to identify leaf blocks in the game world.
     */
    public static final String LEAF_TAG = "leaf";

    private static final Color BASE_LEAF_COLOR = new Color(50, 200, 30);
    private static final int MAX_INT_TO_RANDOM = 4;
    private static final float SCHEDULE_INITIAL_VALUE = 0f;
    private static final float SCHEDULE_FINAL_VALUE = 30f;
    private static final float SCHEDULE_TRANSITION_TIME = 2f;
    private static final float LEAF_TRANSITION_TIME = 2f;
    private static final float MAX_WIDTH_LEAF = 1.1f;

    /**
     * Constructs a Leaf instance.
     *
     * @param topLeftCorner The position of the top-left corner of the leaf.
     */
    public Leaf(Vector2 topLeftCorner) {
        super(topLeftCorner, new RectangleRenderable(BASE_LEAF_COLOR));
        physics().preventIntersectionsFromDirection(null);
        physics().setMass(1);
        scheduleTransition();
        new Transition<>(
                this, this::setDimensions,
                new Vector2(this.getDimensions().x() * MAX_WIDTH_LEAF, this.getDimensions().y()),
                new Vector2(this.getDimensions().x(), this.getDimensions().y()),
                Transition.LINEAR_INTERPOLATOR_VECTOR, LEAF_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null
        );
    }

    /**
     * Schedules a transition for the leaf's rotation to simulate oscillation.
     */
    private void scheduleTransition() {
        float waitTime = generateRandomWaitTime();
        new ScheduledTask(this, waitTime, false, () -> new Transition<>(
                this,
                this.renderer()::setRenderableAngle,
                SCHEDULE_INITIAL_VALUE, SCHEDULE_FINAL_VALUE,
                Transition.LINEAR_INTERPOLATOR_FLOAT, SCHEDULE_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null
        ));
    }

    /**
     * Generates a random wait time for scheduling the leaf's oscillation.
     *
     * @return A random wait time between 0 and MAX_INT_TO_RANDOM.
     */
    private float generateRandomWaitTime() {
        return (float) (Math.random() * MAX_INT_TO_RANDOM);
    }

}
