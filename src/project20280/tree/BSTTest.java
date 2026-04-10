package project20280.tree;

import project20280.interfaces.Entry;
import project20280.interfaces.Position;

import java.util.Random;

public class BSTTest {
    public static void main(String[] args) {
        // Q2: build a BST with 20 distinct random keys in [1, 50)
        TreeMap<Integer, Integer> bst = new TreeMap<>();
        Random rnd = new Random();
        int n_max = 50;
        int n = 20;

        rnd.ints(1, n_max)
                .limit(n)
                .distinct()
                .boxed()
                .forEach(x -> bst.put(x, x));

        // Print the tree shape
        System.out.println("=== BST (n=20) ===");
        System.out.println(bst.toBinaryTreeString());

        // Inorder traversal — should print keys in sorted order
        System.out.println("Inorder traversal:");
        for (Position<Entry<Integer, Integer>> p : bst.tree.inorder()) {
            if (bst.isInternal(p)) {
                System.out.print(p.getElement().getKey() + " ");
            }
        }
        System.out.println();
    }
}