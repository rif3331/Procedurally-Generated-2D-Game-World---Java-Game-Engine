package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sun's halo in the game world.
 * The halo is a glowing effect surrounding the sun.
 */
public class SunHalo extends GameObject {

    private static final Color BASE_SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final String SUN_HALO_TAG = "sunHalo";
    private static final int SUN_HALO_SIZE = 120;

    /**
     * Constructs a SunHalo instance.
     *
     * @param topLeftCorner The top-left corner of the halo.
     * @param dimensions The dimensions of the halo.
     * @param renderable The renderable to use for the halo.
     */
    public SunHalo(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    /**
     * Creates a halo around the sun.
     *
     * @param sun The sun GameObject around which the halo will be created.
     * @return A GameObject representing the sun's halo.
     */
    public static GameObject create(GameObject sun) {
        GameObject sunHalo = new GameObject(sun.getCenter(), new Vector2(SUN_HALO_SIZE, SUN_HALO_SIZE),
                new OvalRenderable(BASE_SUN_HALO_COLOR));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(SUN_HALO_TAG);
        return sunHalo;
    }
}
