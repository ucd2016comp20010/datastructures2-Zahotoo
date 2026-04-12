package project20280.ProjectAssignmentGroup12;

import project20280.interfaces.Entry;
import project20280.tree.AVLTreeMap;

import java.util.*;

public class PerformanceBenchmark {

    private static final int[] SIZES = {100, 500, 1000, 2000, 5000, 10000};
    private static final int WARMUP_RUNS = 3;
    private static final int TIMED_RUNS = 5;

    public static void main(String[] args) {
        System.out.println("=== Q2: Performance Comparison ===");
        System.out.println("Treap vs AVLTreeMap vs java.util.TreeMap");
        System.out.println("Each measurement is the average of " + TIMED_RUNS + " runs after " + WARMUP_RUNS + " warmup runs.\n");

        String[] patterns = {"Random", "Sorted Ascending", "Sorted Descending", "Partially Sorted"};

        for (String pattern : patterns) {
            System.out.println("========================================");
            System.out.println("Input Pattern: " + pattern);
            System.out.println("========================================");

            printHeader();

            for (int n : SIZES) {
                int[] data = generateData(pattern, n);
                benchmarkAll(n, data);
            }
            System.out.println();
        }
    }

    private static void printHeader() {
        System.out.printf("%-8s | %-12s | %-15s | %-15s | %-15s%n",
                "n", "Operation", "Treap (us)", "AVLTreeMap (us)", "TreeMap (us)");
        System.out.println("-".repeat(75));
    }

    private static int[] generateData(String pattern, int n) {
        Random rnd = new Random(12345);
        int[] data = new int[n];

        switch (pattern) {
            case "Random":
                for (int i = 0; i < n; i++) data[i] = rnd.nextInt(n * 10);
                break;
            case "Sorted Ascending":
                for (int i = 0; i < n; i++) data[i] = i;
                break;
            case "Sorted Descending":
                for (int i = 0; i < n; i++) data[i] = n - i;
                break;
            case "Partially Sorted":
                for (int i = 0; i < n; i++) data[i] = i;
                for (int i = 0; i < n / 5; i++) {
                    int a = rnd.nextInt(n);
                    int b = rnd.nextInt(n);
                    int tmp = data[a];
                    data[a] = data[b];
                    data[b] = tmp;
                }
                break;
        }
        return data;
    }

    private static void benchmarkAll(int n, int[] data) {
        long[] treapTimes = benchmarkTreap(data);
        long[] avlTimes = benchmarkAVL(data);
        long[] jtmTimes = benchmarkJavaTreeMap(data);

        String[] ops = {
                "Batch Insert",
                "Single Insert",
                "Search (hit)",
                "Search (miss)",
                "Deletion",
                "Traversal"
        };

        for (int i = 0; i < ops.length; i++) {
            System.out.printf("%-8s | %-12s | %15d | %15d | %15d%n",
                    (i == 0 ? String.valueOf(n) : ""), ops[i], treapTimes[i], avlTimes[i], jtmTimes[i]);
        }
        System.out.println("-".repeat(75));
    }

    private static long[] benchmarkTreap(int[] data) {
        long[] results = new long[6];

        for (int w = 0; w < WARMUP_RUNS; w++) {
            Treap<Integer, Integer> t = new Treap<>();
            for (int d : data) t.put(d, d);
        }

        long totalBatchInsert = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            Treap<Integer, Integer> t = new Treap<>();
            long start = System.nanoTime();
            for (int d : data) t.put(d, d);
            totalBatchInsert += System.nanoTime() - start;
        }
        results[0] = totalBatchInsert / TIMED_RUNS / 1000;

        Treap<Integer, Integer> t = new Treap<>();
        for (int d : data) t.put(d, d);

        long totalSingleInsert = 0;
        int newKey = Integer.MAX_VALUE - 1;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            t.put(newKey - r, newKey - r);
            totalSingleInsert += System.nanoTime() - start;
            t.remove(newKey - r);
        }
        results[1] = totalSingleInsert / TIMED_RUNS / 1000;

        long totalSearchHit = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int d : data) t.get(d);
            totalSearchHit += System.nanoTime() - start;
        }
        results[2] = totalSearchHit / TIMED_RUNS / 1000;

        long totalSearchMiss = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < data.length; i++) t.get(-(i + 1));
            totalSearchMiss += System.nanoTime() - start;
        }
        results[3] = totalSearchMiss / TIMED_RUNS / 1000;

        long totalDelete = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            Treap<Integer, Integer> copy = new Treap<>();
            for (int d : data) copy.put(d, d);
            long start = System.nanoTime();
            for (int d : data) copy.remove(d);
            totalDelete += System.nanoTime() - start;
        }
        results[4] = totalDelete / TIMED_RUNS / 1000;

        long totalTraversal = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (Entry<Integer, Integer> e : t.entrySet()) {
                int k = e.getKey();
            }
            totalTraversal += System.nanoTime() - start;
        }
        results[5] = totalTraversal / TIMED_RUNS / 1000;

        return results;
    }

    private static long[] benchmarkAVL(int[] data) {
        long[] results = new long[6];

        for (int w = 0; w < WARMUP_RUNS; w++) {
            AVLTreeMap<Integer, Integer> t = new AVLTreeMap<>();
            for (int d : data) t.put(d, d);
        }

        long totalBatchInsert = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            AVLTreeMap<Integer, Integer> t = new AVLTreeMap<>();
            long start = System.nanoTime();
            for (int d : data) t.put(d, d);
            totalBatchInsert += System.nanoTime() - start;
        }
        results[0] = totalBatchInsert / TIMED_RUNS / 1000;

        AVLTreeMap<Integer, Integer> t = new AVLTreeMap<>();
        for (int d : data) t.put(d, d);

        long totalSingleInsert = 0;
        int newKey = Integer.MAX_VALUE - 1;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            t.put(newKey - r, newKey - r);
            totalSingleInsert += System.nanoTime() - start;
            t.remove(newKey - r);
        }
        results[1] = totalSingleInsert / TIMED_RUNS / 1000;

        long totalSearchHit = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int d : data) t.get(d);
            totalSearchHit += System.nanoTime() - start;
        }
        results[2] = totalSearchHit / TIMED_RUNS / 1000;

        long totalSearchMiss = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < data.length; i++) t.get(-(i + 1));
            totalSearchMiss += System.nanoTime() - start;
        }
        results[3] = totalSearchMiss / TIMED_RUNS / 1000;

        long totalDelete = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            AVLTreeMap<Integer, Integer> copy = new AVLTreeMap<>();
            for (int d : data) copy.put(d, d);
            long start = System.nanoTime();
            for (int d : data) copy.remove(d);
            totalDelete += System.nanoTime() - start;
        }
        results[4] = totalDelete / TIMED_RUNS / 1000;

        long totalTraversal = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (Entry<Integer, Integer> e : t.entrySet()) {
                int k = e.getKey();
            }
            totalTraversal += System.nanoTime() - start;
        }
        results[5] = totalTraversal / TIMED_RUNS / 1000;

        return results;
    }

    private static long[] benchmarkJavaTreeMap(int[] data) {
        long[] results = new long[6];

        for (int w = 0; w < WARMUP_RUNS; w++) {
            java.util.TreeMap<Integer, Integer> t = new java.util.TreeMap<>();
            for (int d : data) t.put(d, d);
        }

        long totalBatchInsert = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            java.util.TreeMap<Integer, Integer> t = new java.util.TreeMap<>();
            long start = System.nanoTime();
            for (int d : data) t.put(d, d);
            totalBatchInsert += System.nanoTime() - start;
        }
        results[0] = totalBatchInsert / TIMED_RUNS / 1000;

        java.util.TreeMap<Integer, Integer> t = new java.util.TreeMap<>();
        for (int d : data) t.put(d, d);

        long totalSingleInsert = 0;
        int newKey = Integer.MAX_VALUE - 1;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            t.put(newKey - r, newKey - r);
            totalSingleInsert += System.nanoTime() - start;
            t.remove(newKey - r);
        }
        results[1] = totalSingleInsert / TIMED_RUNS / 1000;

        long totalSearchHit = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int d : data) t.get(d);
            totalSearchHit += System.nanoTime() - start;
        }
        results[2] = totalSearchHit / TIMED_RUNS / 1000;

        long totalSearchMiss = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < data.length; i++) t.get(-(i + 1));
            totalSearchMiss += System.nanoTime() - start;
        }
        results[3] = totalSearchMiss / TIMED_RUNS / 1000;

        long totalDelete = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            java.util.TreeMap<Integer, Integer> copy = new java.util.TreeMap<>();
            for (int d : data) copy.put(d, d);
            long start = System.nanoTime();
            for (int d : data) copy.remove(d);
            totalDelete += System.nanoTime() - start;
        }
        results[4] = totalDelete / TIMED_RUNS / 1000;

        long totalTraversal = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (java.util.Map.Entry<Integer, Integer> e : t.entrySet()) {
                int k = e.getKey();
            }
            totalTraversal += System.nanoTime() - start;
        }
        results[5] = totalTraversal / TIMED_RUNS / 1000;

        return results;
    }
}
