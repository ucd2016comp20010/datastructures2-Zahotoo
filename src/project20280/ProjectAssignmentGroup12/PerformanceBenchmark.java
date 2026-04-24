package project20280.ProjectAssignmentGroup12;

import project20280.interfaces.Entry;
import project20280.tree.AVLTreeMap;

import java.util.*;

/**
 * Performance Benchmark
 *
 * Compare:
 *      - Treap
 *      - AVLTreeMap
 *      - java.util.TreeMap
 * Operations:
 *      - Batch Insert
 *      - Single Insert
 *      - Search (hit)
 *      - Search (miss)
 *      - Deletion
 *      - Inorder Traversal
 */
public class PerformanceBenchmark {

    // denser sampling at small sizes where differences show up first
    private static final int[] SIZES = {100, 200, 300, 400, 500,
                                        600, 700, 800, 900, 1000,
                                        1500, 2000, 2500, 3000, 3500,
                                        4000, 4500, 5000, 5500,
                                        6000, 6500, 7000, 7500, 8000,
                                        8500, 9000, 9500, 10000};

    // warmups let the jit settle before timing
    private static final int WARMUP_RUNS = 3;
    private static final int TIMED_RUNS = 5;

    // repeat the single insert operation many times
    private static final int SINGLE_INSERT_REPEATS = 5000;

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
        System.out.printf("%-8s | %-14s | %16s | %16s | %16s%n",
                "n", "Operation", "Treap (us)", "AVLTreeMap (us)", "TreeMap (us)");
        System.out.println("-".repeat(92));
    }

    private static int[] generateData(String pattern, int n) {
        // fixed seed so runs are reproducible
        Random rnd = new Random(12345);
        int[] data = new int[n];

        switch (pattern) {
            case "Random":
                // generate unique random keys by shuffling 0...n-1
                for (int i = 0; i < n; i++) {
                    data[i] = i;
                }
                for (int i = n - 1; i > 0; i--) {
                    int j = rnd.nextInt(i + 1);
                    int tmp = data[i];
                    data[i] = data[j];
                    data[j] = tmp;
                }
                break;
            case "Sorted Ascending":
                // worst case for a plain bst treap priorities should still balance it
                for (int i = 0; i < n; i++) data[i] = i;
                break;
            case "Sorted Descending":
                for (int i = 0; i < n; i++) data[i] = n - i;
                break;
            case "Partially Sorted":
                // start sorted then swap about a fifth of the elements
                for (int i = 0; i < n; i++) data[i] = i;
                for (int i = 0; i < n / 5; i++) {
                    int a = rnd.nextInt(n);
                    int b = rnd.nextInt(n);
                    int tmp = data[a];
                    data[a] = data[b];
                    data[b] = tmp;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown pattern: " + pattern);
        }
        return data;
    }

    private static void benchmarkAll(int n, int[] data) {
        double[] treapTimes = benchmarkTreap(data);
        double[] avlTimes = benchmarkAVL(data);
        double[] jtmTimes = benchmarkJavaTreeMap(data);

        String[] ops = {
                "Batch Insert",
                "Single Insert",
                "Search (hit)",
                "Search (miss)",
                "Deletion",
                "Traversal"
        };

        for (int i = 0; i < ops.length; i++) {
            System.out.printf("%-8s | %-14s | %16.3f | %16.3f | %16.3f%n",
                    (i == 0 ? String.valueOf(n) : ""), ops[i], treapTimes[i], avlTimes[i], jtmTimes[i]);
        }
        System.out.println("-".repeat(92));
    }


    /**
     * ======================
     * Treap Benchmark
     * ======================
     */
    private static double[] benchmarkTreap(int[] data) {
        double[] results = new double[6];

        // warm up
        for (int w = 0; w < WARMUP_RUNS; w++) {
            Treap<Integer, Integer> t = new Treap<>();
            for (int d : data) t.put(d, d);
        }

        // batch insert
        long totalBatchInsert = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            Treap<Integer, Integer> t = new Treap<>();
            long start = System.nanoTime();
            for (int d : data) t.put(d, d);
            totalBatchInsert += System.nanoTime() - start;
        }
        results[0] = totalBatchInsert / (double) TIMED_RUNS / 1000.0;

        Treap<Integer, Integer> t = new Treap<>();
        for (int d : data) t.put(d, d);

        // single insert
        long totalSingleInsert = 0;
        // far from dataset keys to avoid collision
        int newKey = Integer.MAX_VALUE / 4;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < SINGLE_INSERT_REPEATS; i++) {
                int key = newKey + r * SINGLE_INSERT_REPEATS + i;
                // remove after put keeps tree size stable across runs
                t.put(key, key);
                t.remove(key);
            }
            totalSingleInsert += System.nanoTime() - start;
        }
        results[1] = totalSingleInsert / (double) TIMED_RUNS / SINGLE_INSERT_REPEATS / 1000.0;

        // search hit
        long totalSearchHit = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int d : data) t.get(d);
            totalSearchHit += System.nanoTime() - start;
        }
        results[2] = totalSearchHit / (double) TIMED_RUNS / 1000.0;

        // search miss
        long totalSearchMiss = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            // negative keys are guaranteed to miss since data is non negative
            for (int i = 0; i < data.length; i++) t.get(-(i + 1));
            totalSearchMiss += System.nanoTime() - start;
        }
        results[3] = totalSearchMiss / (double) TIMED_RUNS / 1000.0;

        // deletion
        long totalDelete = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            // fresh tree each run so every delete hits a populated tree
            Treap<Integer, Integer> copy = new Treap<>();
            for (int d : data) copy.put(d, d);
            long start = System.nanoTime();
            for (int d : data) copy.remove(d);
            totalDelete += System.nanoTime() - start;
        }
        results[4] = totalDelete / (double) TIMED_RUNS / 1000.0;

        // traversal
        long totalTraversal = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (Entry<Integer, Integer> e : t.entrySet()) {
                // touch the key so the jit cannot strip the loop
                int k = e.getKey();
            }
            totalTraversal += System.nanoTime() - start;
        }
        results[5] = totalTraversal / (double) TIMED_RUNS / 1000.0;

        return results;
    }


    /**
     * =========================
     * AVL Tree Map Benchmark
     * =========================
     */
    private static double[] benchmarkAVL(int[] data) {
        double[] results = new double[6];

        for (int w = 0; w < WARMUP_RUNS; w++) {
            AVLTreeMap<Integer, Integer> t = new AVLTreeMap<>();
            for (int d : data) t.put(d, d);
        }

        // batch insert
        long totalBatchInsert = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            AVLTreeMap<Integer, Integer> t = new AVLTreeMap<>();
            long start = System.nanoTime();
            for (int d : data) t.put(d, d);
            totalBatchInsert += System.nanoTime() - start;
        }
        results[0] = totalBatchInsert / (double) TIMED_RUNS / 1000.0;

        AVLTreeMap<Integer, Integer> t = new AVLTreeMap<>();
        for (int d : data) t.put(d, d);

        // single insert
        long totalSingleInsert = 0;
        int newKey = Integer.MAX_VALUE / 4;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < SINGLE_INSERT_REPEATS; i++) {
                int key = newKey + r * SINGLE_INSERT_REPEATS + i;
                t.put(key, key);
                t.remove(key);
            }
            totalSingleInsert += System.nanoTime() - start;
        }
        results[1] = totalSingleInsert / (double) TIMED_RUNS / SINGLE_INSERT_REPEATS / 1000.0;

        // search hit
        long totalSearchHit = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int d : data) t.get(d);
            totalSearchHit += System.nanoTime() - start;
        }
        results[2] = totalSearchHit / (double) TIMED_RUNS / 1000.0;

        // search miss
        long totalSearchMiss = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < data.length; i++) t.get(-(i + 1));
            totalSearchMiss += System.nanoTime() - start;
        }
        results[3] = totalSearchMiss / (double) TIMED_RUNS / 1000.0;

        // deletion
        long totalDelete = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            AVLTreeMap<Integer, Integer> copy = new AVLTreeMap<>();
            for (int d : data) copy.put(d, d);
            long start = System.nanoTime();
            for (int d : data) copy.remove(d);
            totalDelete += System.nanoTime() - start;
        }
        results[4] = totalDelete / (double) TIMED_RUNS / 1000.0;

        // traversal
        long totalTraversal = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (Entry<Integer, Integer> e : t.entrySet()) {
                int k = e.getKey();
            }
            totalTraversal += System.nanoTime() - start;
        }
        results[5] = totalTraversal / (double) TIMED_RUNS / 1000.0;

        return results;
    }


    /**
     * =============================
     * java.util.TreeMap Benchmark
     * =============================
     */
    private static double[] benchmarkJavaTreeMap(int[] data) {
        double[] results = new double[6];

        for (int w = 0; w < WARMUP_RUNS; w++) {
            java.util.TreeMap<Integer, Integer> t = new java.util.TreeMap<>();
            for (int d : data) t.put(d, d);
        }

        // batch insert
        long totalBatchInsert = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            java.util.TreeMap<Integer, Integer> t = new java.util.TreeMap<>();
            long start = System.nanoTime();
            for (int d : data) t.put(d, d);
            totalBatchInsert += System.nanoTime() - start;
        }
        results[0] = totalBatchInsert / (double) TIMED_RUNS / 1000.0;

        java.util.TreeMap<Integer, Integer> t = new java.util.TreeMap<>();
        for (int d : data) t.put(d, d);

        // single insert
        long totalSingleInsert = 0;
        int newKey = Integer.MAX_VALUE / 4;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < SINGLE_INSERT_REPEATS; i++) {
                int key = newKey + r * SINGLE_INSERT_REPEATS + i;
                t.put(key, key);
                t.remove(key);
            }
            totalSingleInsert += System.nanoTime() - start;
        }
        results[1] = totalSingleInsert / (double) TIMED_RUNS / SINGLE_INSERT_REPEATS / 1000.0;

        // search hit
        long totalSearchHit = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int d : data) t.get(d);
            totalSearchHit += System.nanoTime() - start;
        }
        results[2] = totalSearchHit / (double) TIMED_RUNS / 1000.0;

        // search miss
        long totalSearchMiss = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (int i = 0; i < data.length; i++) t.get(-(i + 1));
            totalSearchMiss += System.nanoTime() - start;
        }
        results[3] = totalSearchMiss / (double) TIMED_RUNS / 1000.0;

        // deletion
        long totalDelete = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            java.util.TreeMap<Integer, Integer> copy = new java.util.TreeMap<>();
            for (int d : data) copy.put(d, d);
            long start = System.nanoTime();
            for (int d : data) copy.remove(d);
            totalDelete += System.nanoTime() - start;
        }
        results[4] = totalDelete / (double) TIMED_RUNS / 1000.0;

        // traversal
        long totalTraversal = 0;
        for (int r = 0; r < TIMED_RUNS; r++) {
            long start = System.nanoTime();
            for (java.util.Map.Entry<Integer, Integer> e : t.entrySet()) {
                int k = e.getKey();
            }
            totalTraversal += System.nanoTime() - start;
        }
        results[5] = totalTraversal / (double) TIMED_RUNS / 1000.0;

        return results;
    }
}