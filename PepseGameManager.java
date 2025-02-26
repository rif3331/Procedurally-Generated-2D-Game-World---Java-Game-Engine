package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Manages the Pepse game, handling initialization, updates, and game world creation.
 */
public class PepseGameManager extends GameManager {
    /** Seed value for procedural generation */
    public static final int SEED = 12345;

    private static final int PADDING_BETWEEN_TREES = 170;
    private static final int CYCLE_OF_TIME = 30;
    private static final int PARTS_TO_DIVIDE = 2;
    private static final float WINDOW_CENTER_RATIO = 0.5f;
    private static final int CLOUD_PARTS_TO_DIVIDE = 10;
    private static final int TREE_ARRAY_SIZE = 2;
    private static final Vector2 UI_TEXT_POSITION = new Vector2(10, 110);
    private static final Vector2 UI_TEXT_SIZE = new Vector2(100, 100);

    /** Stores trees for regeneration during game updates */
    private final HashMap<Integer, Object[]> rememberTrees = new HashMap<>();
    private Avatar avatar;
    private Cloud cloud;
    private Rain rain;
    private WindowController windowController;
    private UserInputListener inputListener;
    private ImageReader imageReader;
    private Terrain currentTerrain;
    private Flora flora;
    private Vector2 currentWorldCordineates;
    private int minRangeOfTree = 0;
    private int maxRangeOfTree = 0;

    /**
     * Main entry point for the Pepse game.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    /**
     * Updates the game state.
     * This method is called every frame to update the game
     * logic based on the avatar's position and world state.
     * @param deltaTime Time elapsed since the last frame, in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xPosition = avatar.getTopLeftCorner().x();
        float windowWidth = windowController.getWindowDimensions().x();
        float distanceToGenerate = windowWidth / PARTS_TO_DIVIDE;
        float distanceFromBeginScreen = xPosition - currentWorldCordineates.x();
        float distanceFromEndScreen = currentWorldCordineates.y() - xPosition;

        if (distanceFromBeginScreen < distanceToGenerate) {
            float newMinX = currentWorldCordineates.x() - distanceToGenerate;
            float newMaxX = currentWorldCordineates.y() - distanceToGenerate;
            Vector2 newRange = new Vector2(newMinX, newMinX + distanceToGenerate);
            genarateRightWorld(newMinX, newMaxX, newRange);
        } else if (distanceFromEndScreen < distanceToGenerate) {
            float newMinX = currentWorldCordineates.x() + distanceToGenerate;
            float newMax = currentWorldCordineates.y() + distanceToGenerate;
            Vector2 newRange = new Vector2(newMax - distanceToGenerate, newMax);
            genarateLeftWorld(newMinX, newMax, newRange);
        }
    }

    /**
     * Initializes the game environment.
     * This method sets up the game window, input listeners, and game objects.
     * @param imageReader Used to read images from disk.
     * @param soundReader Used to read sound effects from disk.
     * @param inputListener Used to listen for user input.
     * @param windowController Controls the game window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.windowController = windowController;
        this.inputListener = inputListener;
        this.imageReader = imageReader;

        minRangeOfTree = (int) Math.ceil(-windowController.getWindowDimensions().x()
                / PARTS_TO_DIVIDE / (float) PADDING_BETWEEN_TREES) * PADDING_BETWEEN_TREES;
        maxRangeOfTree = (int) Math.ceil(windowController.getWindowDimensions().x()
                * PARTS_TO_DIVIDE / (float) PADDING_BETWEEN_TREES) * PADDING_BETWEEN_TREES;
        currentWorldCordineates = new Vector2(minRangeOfTree, maxRangeOfTree);

        createWorld();
        addAvatarToWorld();
    }

    /**
     * Adds the avatar to the game world and initializes the camera.
     */
    private void addAvatarToWorld() {
        avatar = new Avatar(new Vector2(windowController.getWindowDimensions().x() / PARTS_TO_DIVIDE,
                currentTerrain.groundHeightAt(windowController.getWindowDimensions().x())
                        - Avatar.AVATAR_SIZE), inputListener, imageReader, cloud);
        avatar.setJumpListener(rain);
        gameObjects().addGameObject(avatar);
        setCamera(new Camera(avatar, new Vector2(
                windowController.getWindowDimensions().mult(WINDOW_CENTER_RATIO).x()
                        - avatar.getTopLeftCorner().x(),
                windowController.getWindowDimensions().mult(WINDOW_CENTER_RATIO).y()
                        - avatar.getTopLeftCorner().y()),
                windowController.getWindowDimensions(), windowController.getWindowDimensions()));

        TextRenderable textRenderable = new TextRenderable(Integer.toString((int) avatar.getEnergy()));
        avatar.setEnergyUpdateListener(newEnergy -> textRenderable.setString(Integer.toString(newEnergy)));
        GameObject txtObj = new GameObject(UI_TEXT_POSITION, UI_TEXT_SIZE, textRenderable);
        gameObjects().addGameObject(txtObj, Layer.UI);
        txtObj.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /**
     * Creates the initial game world, including terrain, trees, and background elements.
     */
    private void createWorld() {
        GameObject sky = Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
        currentTerrain = new Terrain(new Vector2(currentWorldCordineates.y() - currentWorldCordineates.x(),
                windowController.getWindowDimensions().y()), SEED);
        for (Block block : currentTerrain.createInRange((int) currentWorldCordineates.x(),
                (int) currentWorldCordineates.y())) {
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }
        cloud = new Cloud(windowController.getWindowDimensions());
        for (Block block : cloud.createInRange(0,
                (int) windowController.getWindowDimensions().x() / CLOUD_PARTS_TO_DIVIDE)) {
            gameObjects().addGameObject(block, Layer.BACKGROUND);
        }

        rain = new Rain(gameObjects());
        GameObject sun = Sun.create(windowController.getWindowDimensions(), CYCLE_OF_TIME);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);

        GameObject night = Night.create(windowController.getWindowDimensions(), CYCLE_OF_TIME);
        gameObjects().addGameObject(night, Layer.FOREGROUND);

        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));

        Tree.initializeRandom(SEED);
        this.flora = new Flora(x -> currentTerrain.groundHeightAt(x), SEED);
        HashMap<Tree, TreeData> rangeMap = flora.createInRange((int) currentWorldCordineates.x(),
                (int) currentWorldCordineates.y());
        addTrees(rangeMap);
    }

    /**
     * Checks if the terrain range has expanded, updating the range if necessary.
     * @return True if the range was updated, false otherwise.
     */
    private Boolean checkChangeNewBorder() {
        if (currentWorldCordineates.x() < minRangeOfTree) {
            minRangeOfTree = (int) currentWorldCordineates.x();
            return true;
        }
        if (currentWorldCordineates.y() > maxRangeOfTree) {
            maxRangeOfTree = (int) currentWorldCordineates.y();
            return true;
        }
        return false;
    }

    /**
     * Expands the terrain in the specified range.
     * @param newMinX The minimum x-coordinate of the new range.
     * @param newMaxX The maximum x-coordinate of the new range.
     */
    private void expandTerrain(float newMinX, float newMaxX) {
        List<Block> newBlocks = currentTerrain.createInRange((int) newMinX, (int) newMaxX);
        for (Block block : newBlocks) {
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }
    }

    /**
     * Reduces the terrain outside the specified range.
     * @param newMinX The minimum x-coordinate of the retained range.
     * @param newMaxX The maximum x-coordinate of the retained range.
     */
    private void reduceTerrain(float newMinX, float newMaxX) {
        List<Block> removedBlocks = currentTerrain.filterBlocksInRange((int) newMinX, (int) newMaxX);
        for (Block block : removedBlocks) {
            gameObjects().removeGameObject(block, Layer.STATIC_OBJECTS);
        }
    }

    /**
     * Removes trees outside the specified range and stores them for possible restoration.
     * @param newMinX The minimum x-coordinate of the retained range.
     * @param newMaxX The maximum x-coordinate of the retained range.
     */
    private void reduceTrees(float newMinX, float newMaxX) {
        HashMap<Tree, TreeData> removedTrees = flora.filterTreesOutOfRange((int) newMinX, (int) newMaxX);
        for (Map.Entry<Tree, TreeData> entry : removedTrees.entrySet()) {
            Tree tree = entry.getKey();
            TreeData treeData = entry.getValue();
            int key = (int) tree.getTopLeftCorner().x() / PADDING_BETWEEN_TREES * PADDING_BETWEEN_TREES;
            Object[] treeArray = new Object[TREE_ARRAY_SIZE];
            treeArray[0] = tree;
            treeArray[1] = treeData;
            rememberTrees.put(key, treeArray);
            gameObjects().removeGameObject(tree, Layer.STATIC_OBJECTS);
            for (Leaf leaf : treeData.getLeaves()) {
                gameObjects().removeGameObject(leaf, Layer.STATIC_OBJECTS);
            }
            for (Fruit fruit : treeData.getFruits()) {
                gameObjects().removeGameObject(fruit, Layer.STATIC_OBJECTS);
            }
        }
    }

    /**
     * Adds trees to the game world.
     * @param newTree A map of trees and their data to add.
     */
    private void addTrees(HashMap<Tree, TreeData> newTree) {
        for (Map.Entry<Tree, TreeData> entry : newTree.entrySet()) {
            Tree tree = entry.getKey();
            TreeData treeData = entry.getValue();
            gameObjects().addGameObject(tree, Layer.STATIC_OBJECTS);
            for (Leaf leaf : treeData.getLeaves()) {
                gameObjects().addGameObject(leaf, Layer.STATIC_OBJECTS);
            }
            for (Fruit fruit : treeData.getFruits()) {
                gameObjects().addGameObject(fruit, Layer.STATIC_OBJECTS);
            }
        }
    }

    /**
     * Restores trees within the specified range from stored data.
     * @param newRange The range of x-coordinates to restore trees within.
     */
    private void restoreTrees(Vector2 newRange) {
        Iterator<Map.Entry<Integer, Object[]>> iterator = rememberTrees.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Object[]> entry = iterator.next();
            Integer key = entry.getKey();
            Object[] values = entry.getValue();
            if (key >= newRange.x() && key <= newRange.y()) {
                Tree tree = (Tree) values[0];
                TreeData treeData = (TreeData) values[1];
                flora.createPastTree(tree, treeData);
                gameObjects().addGameObject(tree, Layer.STATIC_OBJECTS);
                for (Leaf leaf : treeData.getLeaves()) {
                    gameObjects().addGameObject(leaf, Layer.STATIC_OBJECTS);
                }
                for (Fruit fruit : treeData.getFruits()) {
                    fruit.setTopLeftCorner(fruit.getInitialPosition());
                    fruit.renderer().setRenderable(new OvalRenderable(Color.RED));
                    gameObjects().addGameObject(fruit, Layer.STATIC_OBJECTS);
                }
                iterator.remove();
            }
        }
    }

    /**
     * Generates and manages the world on the right side of the screen.
     * @param newMinX The new minimum x-coordinate.
     * @param newMax The new maximum x-coordinate.
     * @param newRange The range of coordinates to generate.
     */
    private void genarateRightWorld(float newMinX, float newMax, Vector2 newRange) {
        currentWorldCordineates = new Vector2(newMinX, newMax);
        Boolean bordersGrow = checkChangeNewBorder();
        expandTerrain(newMinX, newMax);
        if (bordersGrow) {
            float nextTreePosition = newRange.x();
            while (nextTreePosition < newRange.y()) {
                addTrees(flora.createInRange((int) nextTreePosition,
                        (int) nextTreePosition + Tree.WIDTH_TREE));
                nextTreePosition += PADDING_BETWEEN_TREES;
            }
        } else {
            restoreTrees(newRange);
        }
        reduceTerrain(newMinX, newMax);
        reduceTrees(newMinX, newMax);
    }

    /**
     * Generates and manages the world on the left side of the screen.
     * @param newMinX The new minimum x-coordinate.
     * @param newMax The new maximum x-coordinate.
     * @param newRange The range of coordinates to generate.
     */
    private void genarateLeftWorld(float newMinX, float newMax, Vector2 newRange) {
        currentWorldCordineates = new Vector2(newMinX, newMax);
        Boolean bordersGrow = checkChangeNewBorder();
        expandTerrain(newMinX, newMax);
        if (bordersGrow) {
            float nextTreePosition = newRange.y();
            while (nextTreePosition > newRange.x()) {
                addTrees(flora.createInRange((int) nextTreePosition,
                        (int) nextTreePosition + Tree.WIDTH_TREE));
                nextTreePosition -= PADDING_BETWEEN_TREES;
            }
        } else {
            restoreTrees(newRange);
        }
        reduceTerrain(newMinX, newMax);
        reduceTrees(newMinX, newMax);
    }
}
