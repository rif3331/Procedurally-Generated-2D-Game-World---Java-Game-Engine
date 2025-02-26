package pepse.world.trees;

import java.util.List;

/**
 * A data container for a tree's associated fruits and leaves.
 */
public class TreeData {

    private final List<Fruit> fruits;
    private final List<Leaf> leaves;

    /**
     * Constructs a TreeData instance.
     *
     * @param fruits A list of fruits associated with the tree.
     * @param leaves A list of leaves associated with the tree.
     */
    public TreeData(List<Fruit> fruits, List<Leaf> leaves) {
        this.fruits = fruits;
        this.leaves = leaves;
    }

    /**
     * Gets the list of fruits associated with the tree.
     *
     * @return A list of Fruit objects.
     */
    public List<Fruit> getFruits() {
        return fruits;
    }

    /**
     * Gets the list of leaves associated with the tree.
     *
     * @return A list of Leaf objects.
     */
    public List<Leaf> getLeaves() {
        return leaves;
    }
}
