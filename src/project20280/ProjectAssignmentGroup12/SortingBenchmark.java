package project20280.ProjectAssignmentGroup12;

import project20280.interfaces.Entry;
import project20280.priorityqueue.HeapPriorityQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Q3 sorting benchmark.
 */
public class SortingBenchmark {

    private static final int[] SIZES = {100, 200, 300, 400, 500,
                                        600, 700, 800, 900, 1000,
                                        1500, 2000, 2500, 3000, 3500,
                                        4000, 4500, 5000, 5500,
                                        6000, 6500, 7000, 7500, 8000,
                                        8500, 9000, 9500, 10000};

    private static final int WARMUP_RUNS = 3;
    private static final int TIMED_RUNS = 5;

    public static void main(String[] args) {
        System.out.println("=== Q3: Sorting Benchmark ===");
        System.out.println("TreapSort vs PQSort vs Collections.sort vs QuickSort vs MergeSort");
        System.out.println("Each measurement is the average of " + TIMED_RUNS + " runs after " + WARMUP_RUNS
                + " warmup runs.\n");

        String[] patterns = {"Random", "Nearly Sorted", "Reverse Sorted"};

        for (String pattern : patterns) {
            System.out.println("========================================");
            System.out.println("Input Pattern: " + pattern);
            System.out.println("========================================");
            printHeader();

            for (int n : SIZES) {
                Integer[] data = genData(pattern, n);
                double treap = bench(data, SortingBenchmark::treapSort);
                double pq = bench(data, SortingBenchmark::pqSort);
                double javaSort = bench(data, SortingBenchmark::javaSort);
                double quick = bench(data, SortingBenchmark::quickSort);
                double merge = bench(data, SortingBenchmark::mergeSort);
                printRow(n, treap, pq, javaSort, quick, merge);
            }
            System.out.println();
        }
    }

    private static void printHeader() {
        System.out.printf("%-8s | %16s | %16s | %20s | %16s | %16s%n",
                "n", "TreapSort (us)", "PQSort (us)", "Collections.sort(us)", "QuickSort (us)", "MergeSort (us)");
        System.out.println("-".repeat(106));
    }

    private static void printRow(int n, double treap, double pq, double javaSort, double quick, double merge) {
        System.out.printf("%-8d | %16.3f | %16.3f | %20.3f | %16.3f | %16.3f%n",
                n, treap, pq, javaSort, quick, merge);
    }

    private static Integer[] genData(String pattern, int n) {
        Integer[] data = new Integer[n];
        for (int i = 0; i < n; i++) {
            data[i] = i;
        }

        Random rnd = new Random(12345);
        switch (pattern) {
            case "Random":
                shuffle(data, rnd);
                break;
            case "Nearly Sorted":
                int swaps = Math.max(1, n / 10);
                for (int i = 0; i < swaps; i++) {
                    int a = rnd.nextInt(n);
                    int b = rnd.nextInt(n);
                    swap(data, a, b);
                }
                break;
            case "Reverse Sorted":
                for (int i = 0; i < n; i++) {
                    data[i] = n - 1 - i;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown pattern: " + pattern);
        }
        return data;
    }

    private static double bench(Integer[] data, Consumer<Integer[]> sorter) {
        for (int w = 0; w < WARMUP_RUNS; w++) {
            Integer[] copy = copy(data);
            sorter.accept(copy);
        }

        long total = 0;
        for (int t = 0; t < TIMED_RUNS; t++) {
            Integer[] copy = copy(data);
            long start = System.nanoTime();
            sorter.accept(copy);
            total += System.nanoTime() - start;
        }

        return total / (double) TIMED_RUNS / 1000.0;
    }

    public static void treapSort(Integer[] data) {
        Treap<Integer, Integer> treap = new Treap<>();

        // Count duplicates.
        for (Integer x : data) {
            Integer count = treap.get(x);
            treap.put(x, count == null ? 1 : count + 1);
        }

        int j = 0;
        for (Entry<Integer, Integer> e : treap.entrySet()) {
            for (int c = 0; c < e.getValue(); c++) {
                data[j++] = e.getKey();
            }
        }
    }

    public static void pqSort(Integer[] data) {
        HeapPriorityQueue<Integer, Integer> pq = new HeapPriorityQueue<>();
        pq.pqSort(data);
    }

    public static void javaSort(Integer[] data) {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(data));
        Collections.sort(list);
        list.toArray(data);
    }

    public static void quickSort(Integer[] data) {
        quickSortRec(data, 0, data.length - 1);
    }

    private static void quickSortRec(Integer[] data, int left, int right) {
        if (left >= right) {
            return;
        }

        int i = left, j = right;
        int middle = left + (right - left) / 2;
        int pivot = data[middle];

        while (i <= j) {
            while (data[i] < pivot) {
                i++;
            }
            while (data[j] > pivot) {
                j--;
            }
            if (i <= j) {
                swap(data, i++, j--);
            }
        }

        if (left < j) {
            quickSortRec(data, left, j);
        }
        if (i < right) {
            quickSortRec(data, i, right);
        }
    }

    public static void mergeSort(Integer[] data) {
        if (data.length < 2) {
            return;
        }

        // Reuse one temp array for the full recursion.
        Integer[] temp = new Integer[data.length];
        mergeSortRec(data, temp, 0, data.length - 1);
    }

    private static void mergeSortRec(Integer[] data, Integer[] temp, int left, int right) {
        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;
        mergeSortRec(data, temp, left, middle);
        mergeSortRec(data, temp, middle + 1, right);
        merge(data, temp, left, middle, right);
    }

    private static void merge(Integer[] data, Integer[] temp, int left, int middle, int right) {
        int i = left, j = middle + 1, k = left;
        while (i <= middle && j <= right) {
            if (data[i] <= data[j]) {
                temp[k++] = data[i++];
            } else {
                temp[k++] = data[j++];
            }
        }

        while (i <= middle) {
            temp[k++] = data[i++];
        }

        while (j <= right) {
            temp[k++] = data[j++];
        }

        for (int p = left; p <= right; p++) {
            data[p] = temp[p];
        }
    }

    private static Integer[] copy(Integer[] data) {
        return Arrays.copyOf(data, data.length);
    }

    private static void shuffle(Integer[] data, Random rnd) {
        for (int i = data.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            swap(data, i, j);
        }
    }

    private static void swap(Integer[] data, int i, int j) {
        Integer tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }
}
