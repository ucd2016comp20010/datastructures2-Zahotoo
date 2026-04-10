package project20280.tree;

import project20280.interfaces.Entry;
import java.util.*;

public class BSTSortTest {

    // Sort using plain TreeMap (BST)
    static List<Integer> bstSort(List<Integer> input) {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        for (Integer x : input) map.put(x, x);
        List<Integer> result = new ArrayList<>();
        for (Entry<Integer, Integer> e : map.entrySet()) result.add(e.getKey());
        return result;
    }

    // Sort using AVLTreeMap
    static List<Integer> avlSort(List<Integer> input) {
        AVLTreeMap<Integer, Integer> map = new AVLTreeMap<>();
        for (Integer x : input) map.put(x, x);
        List<Integer> result = new ArrayList<>();
        for (Entry<Integer, Integer> e : map.entrySet()) result.add(e.getKey());
        return result;
    }

    // Sort using java.util.TreeMap (also a red-black tree, O(n log n) guaranteed)
    static List<Integer> javaSort(List<Integer> input) {
        java.util.TreeMap<Integer, Integer> map = new java.util.TreeMap<>();
        for (Integer x : input) map.put(x, x);
        return new ArrayList<>(map.keySet());
    }

    public static void main(String[] args) {
        Random rnd = new Random();
        int[] sizes = {100, 1000, 5000, 10000, 50000};

        System.out.printf("%-10s %-15s %-15s %-15s%n", "n", "BST (ms)", "AVL (ms)", "java.util (ms)");
        System.out.println("-".repeat(57));

        for (int n : sizes) {
            // Generate n distinct random integers
            List<Integer> data = new ArrayList<>();
            rnd.ints(0, n * 10).distinct().limit(n).forEach(data::add);

            long t1 = System.currentTimeMillis();
            bstSort(data);
            long bstTime = System.currentTimeMillis() - t1;

            long t2 = System.currentTimeMillis();
            avlSort(data);
            long avlTime = System.currentTimeMillis() - t2;

            long t3 = System.currentTimeMillis();
            javaSort(data);
            long javaTime = System.currentTimeMillis() - t3;

            System.out.printf("%-10d %-15d %-15d %-15d%n", n, bstTime, avlTime, javaTime);
        }
    }
}