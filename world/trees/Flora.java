package pepse.world.trees;

import danogl.util.Vector2;
import pepse.world.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * Handles the creation and management of trees, leaves, and fruits in the game world.
 */
public class Flora {

    private static final float PADDING_TREES = (float) (Tree.WIDTH_TREE - Block.SIZE * 5) / 2;
    private static final int WINDOW_CENTER_RATIO = 2;
    private static final int TREES_IN_RANGE_JUMPS = 850;
    private static final float ROUND_X_RANGE = 100;
    private static final int ROWS_AND_COLS_OF_TREE = 5;
    private final Function<Float, Float> groundHeightAt;
    private final HashMap<Tree, TreeData> treeMap = new HashMap<>();
    private final Random random;

    /**
     * Constructs a Flora instance.
     *
     * @param groundHeightAt A function that returns the ground height at a given x-coordinate.
     * @param seed The seed for random generation of trees, leaves, and fruits.
     */
    public Flora(Function<Float, Float> groundHeightAt, int seed) {
        this.random = new Random(seed);
        this.groundHeightAt = groundHeightAt;
    }

    /**
     * Creates leaves and fruits for a given tree.
     *
     * @param startX The starting x-coordinate for creating leaves and fruits.
     * @param startY The starting y-coordinate for creating leaves and fruits.
     * @param tree The tree to associate with the leaves and fruits.
     * @param leafList The list to populate with created leaves.
     * @param fruitList The list to populate with created fruits.
     * @return A TreeData object containing the created leaves and fruits.
     */
    public TreeData createLeafsAndFruit(int startX, int startY, Tree tree,
                                        List<Leaf> leafList, List<Fruit> fruitList) {
        for (int i = 0; i < ROWS_AND_COLS_OF_TREE; i++) {
            for (int j = 0; j < ROWS_AND_COLS_OF_TREE; j++) {
                boolean isntLeaf = random.nextBoolean();
                boolean isntFruit = random.nextBoolean();
                if (isntLeaf && isntFruit) {
                    continue;
                }
                if (!isntLeaf) {
                    int xPos = startX + j * Block.SIZE;
                    int yPos = (int) (startY + i * Block.SIZE -
                            tree.getDimensions().y() / WINDOW_CENTER_RATIO);
                    Leaf leaf = new Leaf(new Vector2(xPos, yPos));
                    leaf.setTag(Leaf.LEAF_TAG);
                    leafList.add(leaf);
                }
                if (!isntFruit) {
                    int xPos = startX + j * Block.SIZE;
                    int yPos = (int) (startY + i * Block.SIZE -
                            tree.getDimensions().y() / WINDOW_CENTER_RATIO);
                    Fruit fruit = new Fruit(new Vector2(xPos, yPos));
                    fruit.setTag(Fruit.FRUIT_TAG);
                    fruitList.add(fruit);
                }
            }
        }
        return new TreeData(fruitList, leafList);
    }

    /**
     * Associates a previously created tree with its TreeData.
     *
     * @param tree The tree to associate.
     * @param treeData The data (leaves and fruits) associated with the tree.
     */
    public void createPastTree(Tree tree, TreeData treeData) {
        treeMap.put(tree, treeData);
    }

    /**
     * Creates trees, leaves, and fruits within a specified range.
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A HashMap mapping created trees to their associated TreeData.
     */
    public HashMap<Tree, TreeData> createInRange(int minX, int maxX) {
        int roundedMinX = (int) (Math.ceil(minX / ROUND_X_RANGE) * ROUND_X_RANGE);
        int roundedMaxX = (int) (Math.floor(maxX / ROUND_X_RANGE) * ROUND_X_RANGE);
        for (int x = roundedMinX; x <= roundedMaxX; x += TREES_IN_RANGE_JUMPS) {
            Vector2 heightByX = new Vector2(x, groundHeightAt.apply((float) x));
            Tree tree = new Tree(heightByX);
            tree.setTopLeftCorner(new Vector2(tree.getTopLeftCorner().x(),
                    tree.getTopLeftCorner().y() - tree.getDimensions().y()));
            tree.setTag(Tree.Tree_TAG);
            List<Leaf> leafList = new ArrayList<>();
            List<Fruit> fruitList = new ArrayList<>();
            int startX = (int) (tree.getTopLeftCorner().x() + PADDING_TREES);
            int startY = (int) (tree.getTopLeftCorner().y() - Block.SIZE);
            treeMap.put(tree, createLeafsAndFruit(startX, startY, tree, leafList, fruitList));
        }
        return treeMap;
    }

    /**
     * Filters and removes trees that are outside the specified range.
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A HashMap of removed trees and their associated TreeData.
     */
    public HashMap<Tree, TreeData> filterTreesOutOfRange(int minX, int maxX) {
        HashMap<Tree, TreeData> removedTrees = new HashMap<>();
        treeMap.entrySet().removeIf(entry -> {
            Tree tree = entry.getKey();
            int x = (int) tree.getTopLeftCorner().x();
            if (x < minX || x > maxX) {
                removedTrees.put(tree, entry.getValue());
                return true;
            }
            return false;
        });
        return removedTrees;
    }
}
