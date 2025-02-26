package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sun in the game world.
 * The sun moves in a circular path to simulate the day-night cycle.
 */
public class Sun {

    private static final int SUN_SIZE = 100;
    private static final String SUN_TAG = "sun";
    private static final float MAX_ANGLE = 360;
    private static final float SUN_TRANSITION_INITIAL_VALUE = 0f;
    private static final float WINDOW_PARTS_Y = 3f;
    private static final int WINDOW_PARTS_X = 2;

    /**
     * Creates a sun object that moves in a circular path to simulate the day-night cycle.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength The duration of a full day-night cycle, in seconds.
     * @return A GameObject representing the sun.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        Vector2 initialSunCenter = new Vector2(windowDimensions.x() / WINDOW_PARTS_X,
                windowDimensions.y() / WINDOW_PARTS_Y);
        Vector2 cycleCenter = new Vector2(windowDimensions.x() / WINDOW_PARTS_X,
                windowDimensions.y() / WINDOW_PARTS_Y * WINDOW_PARTS_X);
        GameObject sun = new GameObject(initialSunCenter,
                new Vector2(SUN_SIZE, SUN_SIZE), new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);
        new Transition<>(sun, (Float angle)
                -> sun.setCenter(initialSunCenter.subtract(cycleCenter).rotated(angle).add(cycleCenter)),
                SUN_TRANSITION_INITIAL_VALUE, MAX_ANGLE, Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null);
        return sun;
    }
}
