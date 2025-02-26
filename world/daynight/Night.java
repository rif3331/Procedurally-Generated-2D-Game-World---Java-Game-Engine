package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the night overlay in the game world.
 * This overlay gradually transitions in opacity to simulate the passage of time and nightfall.
 */
public class Night {

    private static final float MIDNIGHT_OPACITY = 0.5f;
    private static final float NIGHT_TRANSITION_INITIAL_VALUE = 0f;
    private static final int PART_OF_CYCLE = 2;
    private static final String NIGHT_TAG = "night";

    /**
     * Creates a night overlay that transitions its opacity over a given cycle length.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength The duration of a full day-night cycle, in seconds.
     * @return A GameObject representing the night overlay.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        GameObject night = new GameObject(Vector2.ZERO,
                windowDimensions, new RectangleRenderable(Color.black));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);
        new Transition<>(
                night,
                night.renderer()::setOpaqueness,
                NIGHT_TRANSITION_INITIAL_VALUE,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength / PART_OF_CYCLE,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
        return night;
    }
}
