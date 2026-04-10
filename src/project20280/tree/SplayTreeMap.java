package project20280.tree;

import project20280.interfaces.Entry;
import project20280.interfaces.Position;

import java.util.Comparator;

public class SplayTreeMap<K, V> extends TreeMap<K, V> {

    /**
     * Constructs an empty map using the natural ordering of keys.
     */
    public SplayTreeMap() {
        super();
    }

    /**
     * Constructs an empty map using the given comparator to order keys.
     *
     * @param comp comparator defining the order of keys in the map
     */
    public SplayTreeMap(Comparator<K> comp) {
        super(comp);
    }

    /**
     * Utility used to rebalance after a map operation.
     */
    private void splay(Position<Entry<K, V>> p) {
        while (!isRoot(p)) {
            Position<Entry<K, V>> parent = parent(p);
            Position<Entry<K, V>> grand = parent(parent);

            if (grand == null) {
                // zig: parent is root - one single rotation brings p to root
                rotate(p);
            } else if (parent == left(grand) == (p == left(parent))) {
                // zig-zig: p and parent are on the same side
                // rotate parent first , then p
                rotate(parent);
                rotate(p);
            } else {
                // zig-zag: p and parent are on different sides
                // rotate p twice
                rotate(p);
                rotate(p);
            }
        }
    }

    /**
     * Overrides the TreeMap rebalancing hook that is called after a node access.
     *
     * @param p
     */
    //@Override
    protected void rebalanceAccess(Position<Entry<K, V>> p) {
        // TODO
        if (isExternal(p)) {
            p = parent(p);
        }
        if (p != null) {
            splay(p);
        }
    }

    /**
     * Overrides the TreeMap rebalancing hook that is called after an insertion.
     *
     * @param p
     */
    //@Override
    protected void rebalanceInsert(Position<Entry<K, V>> p) {
        // TODO
        splay(p);
    }

    /**
     * Overrides the TreeMap rebalancing hook that is called after a deletion.
     *
     * @param p
     */
    //@Override
    protected void rebalanceDelete(Position<Entry<K, V>> p) {
        // TODO
        if (isEmpty()) return;
        if (isInternal(p)) {
            splay(p);
        } else if (!isRoot(p)) {
            splay(parent(p));
        }
    }

    public static void main(String[] args) {
        SplayTreeMap<Integer, Integer> treeMap = new SplayTreeMap<Integer, Integer>();

        Integer[] arr = new Integer[]{44, 17, 88, 8, 32, 65, 97, 28, 54, 82, 93, 21, 29, 76, 80};
        for (Integer i : arr)
            treeMap.put(i, i);
        System.out.println("treeMap \n" + treeMap.toBinaryTreeString());

        // After accessing 65, it should move to (or near) the root
        treeMap.get(65);
        System.out.println("After get(65): \n" + treeMap.toBinaryTreeString());
    }
}
