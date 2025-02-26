package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.trees.Fruit;

import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * Represents the avatar controlled by the player in the game.
 */
public class Avatar extends GameObject {

    /**
     * Size of the avatar in pixels.
     */
    public static final int AVATAR_SIZE = 50;
    private static final int VELOCITY_X = 400;
    private static final int VELOCITY_Y = -650;
    private static final int GRAVITY = 600;
    private static final float TIME_BETWEEN_CLIPS = 0.1f;
    private static final float MOVE_ENERGY = 0.5f;
    private static final float JUMP_ENERGY = 10f;
    private static final float MAX_ENERGY = 100f;
    private static final String FIRST_IMG = "assets/idle_0.png";
    private static final String AVATAR_TAG = "avatar";
    private final UserInputListener inputListener;
    private EnergyUpdateListener energyUpdateListener;
    private JumpListener jumpListener;
    private AnimationRenderable currentAnimation;
    private final ImageReader imageReader;
    private final Cloud cloud;
    private float energy = 100;
    private static final String[] SERIES_STANDING_IMAGES = new String[]{"assets/idle_0.png",
            "assets/idle_1.png", "assets/idle_2.png", "assets/idle_3.png"};
    private static final String[] SERIES_JUMP_IMAGES = new String[]{"assets/jump_0.png",
            "assets/jump_1.png", "assets/jump_2.png", "assets/jump_3.png"};
    private static final String[] SERIES_RUN_IMAGES =
            new String[]{"assets/run_0.png", "assets/run_1.png",
                    "assets/run_2.png", "assets/run_3.png", "assets/run_4.png", "assets/run_5.png"};

    /**
     * Loads images from file paths.
     * @param fileNames Array of file paths for images.
     * @return An array of Renderable objects corresponding to the images.
     */
    public Renderable[] loadImages(String[] fileNames) {
        Renderable[] images = new Renderable[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            images[i] = imageReader.readImage(fileNames[i], true);
        }
        return images;
    }

    /**
     * Constructs a new Avatar instance.
     * @param topLeftCorner The top-left corner position of the avatar.
     * @param inputListener Listener for user input.
     * @param imageReader Reader for loading images.
     * @param cloud The cloud object interacting with the avatar.
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener,
                  ImageReader imageReader, Cloud cloud) {
        super(topLeftCorner, Vector2.ONES.mult(AVATAR_SIZE),
                imageReader.readImage(FIRST_IMG, true));
        this.imageReader = imageReader;
        this.inputListener = inputListener;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        currentAnimation = new AnimationRenderable(loadImages(SERIES_STANDING_IMAGES), TIME_BETWEEN_CLIPS);
        this.renderer().setRenderable(currentAnimation);
        this.cloud = cloud;
        setTag(AVATAR_TAG);
    }

    /**
     * Sets the listener for energy updates.
     * @param listener Listener to handle energy updates.
     */
    public void setEnergyUpdateListener(EnergyUpdateListener listener) {
        this.energyUpdateListener = listener;
    }

    /**
     * Sets the listener for jump actions.
     * @param listener Listener to handle jump actions.
     */
    public void setJumpListener(JumpListener listener) {
        this.jumpListener = listener;
    }

    /**
     * Retrieves the current energy level of the avatar.
     * @return The energy level.
     */
    public float getEnergy() {
        return energy;
    }

    /**
     * Updates the avatar's state.
     * Handles movement, jumping, and energy consumption.
     * @param deltaTime Time elapsed since the last frame, in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT) &&
                !inputListener.isKeyPressed(KeyEvent.VK_RIGHT) && energy >= MOVE_ENERGY) {
            xVel -= VELOCITY_X;energy -= MOVE_ENERGY;
            currentAnimation = new AnimationRenderable(loadImages(SERIES_RUN_IMAGES), TIME_BETWEEN_CLIPS);
            this.renderer().setIsFlippedHorizontally(true);
            this.renderer().setRenderable(currentAnimation);
        } else if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)
                && !inputListener.isKeyPressed(KeyEvent.VK_LEFT) && energy >= MOVE_ENERGY) {
            xVel += VELOCITY_X;energy -= MOVE_ENERGY;
            currentAnimation = new AnimationRenderable(loadImages(SERIES_RUN_IMAGES), TIME_BETWEEN_CLIPS);
            this.renderer().setIsFlippedHorizontally(false);
            this.renderer().setRenderable(currentAnimation);
        }

        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0 && energy >= JUMP_ENERGY){
            transform().setVelocityY(VELOCITY_Y);energy -= JUMP_ENERGY;
            currentAnimation = new AnimationRenderable(loadImages(SERIES_JUMP_IMAGES), TIME_BETWEEN_CLIPS);
            this.renderer().setRenderable(currentAnimation);
            if (jumpListener != null) {
                jumpListener.onJump(cloud.getCloudPosition());
            }
        }

        if (energy < MAX_ENERGY && getVelocity().y() == 0 && (inputListener.pressedKeys().isEmpty() ||
                (inputListener.isKeyPressed(KeyEvent.VK_LEFT) &&
                        inputListener.isKeyPressed(KeyEvent.VK_RIGHT)))) {
            currentAnimation = new AnimationRenderable(loadImages(SERIES_STANDING_IMAGES),
                    TIME_BETWEEN_CLIPS);
            this.renderer().setRenderable(currentAnimation);
            energy += 1;
        }
        transform().setVelocityX(xVel);
        if (energyUpdateListener != null) {
            energyUpdateListener.onEnergyUpdated((int) energy);
        }
    }


    /**
     * Handles collision events for the avatar.
     * @param other The other GameObject involved in the collision.
     * @param collision Collision details.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if (Objects.equals(other.getTag(), Fruit.FRUIT_TAG)) {
            if (energy < MAX_ENERGY - JUMP_ENERGY) {
                energy += JUMP_ENERGY;
            } else if (energy >= MAX_ENERGY - JUMP_ENERGY) {
                energy = MAX_ENERGY;
            }
            ((Fruit) other).restart();
        }
        if (Objects.equals(other.getTag(), Terrain.GROUND_TAG)) {
            Vector2 playerPosition = this.getTopLeftCorner();
            Vector2 groundPosition = other.getTopLeftCorner();
            if (playerPosition.y() + this.getDimensions().y() > groundPosition.y()) {
                this.setTopLeftCorner(new Vector2(playerPosition.x(),
                        groundPosition.y() - this.getDimensions().y()));
            }
        }
    }
}
