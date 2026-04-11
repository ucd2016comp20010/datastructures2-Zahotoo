package project20280.ProjectAssignmentGroup12;

import project20280.interfaces.Entry;
import project20280.interfaces.Position;

import java.util.Comparator;
import java.util.Random;

import project20280.tree.BinaryTreePrinter;
import project20280.tree.TreeMap;

/**
 * Treap: Tree + Heap which is a randomised binary search tree
 * This class is the Treap code implementation.
 *      Each node in a Treap has:
 *          - A key (which follows BST property)
 *          - A priority (random value that follows heap property) The priority ensures probabilistic balancing of the tree
 */
public class Treap<K, V> extends TreeMap<K, V> {

    private static final Random rnd = new Random();
    private static final int maxPriority = 50000;

    /**
     * Constructs an empty map using the natural ordering of keys
     */
    public Treap() {
        super();
    }

    /**
     * Constructs an empty map using the given comparator to order keys.
     * @param comp comparator defining the order of keys in the map.
     */
    public Treap(Comparator<K> comp) {
        super(comp);
    }

    /**
     * priority helper function - to get the priority from the given position
     * @param p the position of the node
     * @return the priority of the p
     */
    private int priority(Position<Entry<K, V>> p) {
        // if the p is the leaf node, give it a huge priority value
        // it is helpful to avoid the external node move above the internal node
        if (isExternal(p)) {
            return maxPriority;
        }
        // Each node has a pocket to store their property
        // node
        //   |- key
        //   |- value
        //   |- left/right
        //   |- aux     and in the Treap, the aux store the priority, you can assume that getAux() = getPriority()
        return tree.getAux(p);
    }

    /**
     * Overrides the Treap rebalancing hook that is called after an insertion
     * After BST insert: assign a random priority, then bubble the new node upward
     * until the heap property is restored
     */
    @Override
    protected void rebalanceInsert(Position<Entry<K, V>> p) {
        // give p a random priority
        tree.setAux(p, rnd.nextInt(maxPriority));

        // if p's priority is smaller than its parent, rotate
        while (!isRoot(p)) {
            if (priority(p) < priority(parent(p))) {
                rotate(p);
            } else {
                break;
            }
        }
    }

    /**
     * Overrides the Treap rebalancing hook that is called after a deletion
     */
    @Override
    protected void rebalanceDelete(Position<Entry<K, V>> p) {
        // leave empty
    }

    /**
     * Override the Treap rebalancing hook that is called after an access
     */
    @Override
    protected void rebalanceAccess(Position<Entry<K, V>> p) {
        // leave empty
    }

    /**
     * Override the remove method, after remove operation,
     * the tree need to hold the heap property
     * @param key the key whose entry is to be removed from the map
     * @return the previous value associated with the removed key, or null if no such entry exists
     */
    @Override
    public V remove(K key) throws IllegalArgumentException {
        checkKey(key);
        Position<Entry<K, V>> p = treeSearch(root(), key);

        if (isExternal(p)) {
            return null;
        }

        V removed = p.getElement().getValue();

        while (isInternal(left(p)) && isInternal(right(p))) {
            if (priority(left(p)) < priority(right(p))) {
                rotate(left(p));
            } else {
                rotate(right(p));
            }
        }

        Position<Entry<K, V>> leaf = isExternal(left(p)) ? left(p) : right(p);
        remove(leaf);
        remove(p);

        return removed;
    }

    /**
     * Determine that every internal node satisfies the min-heap property
     * @return true if they are valid
     */
    public boolean isHeap() {
        for (Position<Entry<K, V>> p : tree.positions()) {
            if (isInternal(p)) {
                // in the min-heap, the priority should be bigger from top to bottom
                // if the priority value of child of p is less than p, it violates the heap property
                if (isInternal(left(p)) && priority(left(p)) < priority(p)) {
                    return false;
                }
                if (isInternal(right(p)) && priority(right(p)) < priority(p)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determine the BST property: inorder traversal should give keys in an sorted ascending order
     * @return true if the tree satisfies the BST properties
     */
    public boolean isBST() {
        // define the last visited key
        K prev = null;

        for (Entry<K, V> e : entrySet()) {
            // if the previous key is greater than the current key,
            // the function will return false
            // in inorder traversal for BST, the keys should in an strictly sorted ascending order.
            if (prev != null && compare(prev, e.getKey()) >= 0) {
                return false;
            }
            // move to next
            prev = e.getKey();
        }
        return true;
    }

    public String toBinaryTreeString() {
        BinaryTreePrinter<Entry<K, V>> btp = new BinaryTreePrinter<>(this.tree);
        return btp.print();
    }

    /**
     * this method is used to the main method for testing
     */
    public void printEntriesPriority() {
        for (Position<Entry<K, V>> p : tree.positions()) {
            if (isInternal(p)) {
                System.out.println("key = " + p.getElement().getKey() + ", priority = " + priority(p));
            }
        }
    }

    public static void main(String[] args) {
        Treap<Integer, Integer> treap = new Treap<>();
        Integer[] arr = {5, 3, 7, 1, 4, 6, 8, 2};

        for (Integer i : arr) treap.put(i, i);

        System.out.println("Tree shape:");
        System.out.println(treap.toBinaryTreeString());
        System.out.println("Inorder (should be sorted): " + treap.entrySet());
        System.out.println("Heap valid: " + treap.isHeap());
        System.out.println("BST valid:  " + treap.isBST());
        treap.printEntriesPriority();

        treap.remove(5);
        System.out.println("\nAfter remove(5):");
        System.out.println(treap.toBinaryTreeString());
        System.out.println("Inorder: " + treap.entrySet());
        System.out.println("Heap valid: " + treap.isHeap());
        System.out.println("BST valid:  " + treap.isBST());
        treap.printEntriesPriority();
    }
}
