package project20280.priorityqueue;

/*
 */

import project20280.interfaces.Entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 * An implementation of a priority queue using an array-based heap.
 */

public class HeapPriorityQueue<K, V> extends AbstractPriorityQueue<K, V> {

    protected ArrayList<Entry<K, V>> heap = new ArrayList<>();

    /**
     * Creates an empty priority queue based on the natural ordering of its keys.
     */
    public HeapPriorityQueue() {
        super();
    }

    /**
     * Creates an empty priority queue using the given comparator to order keys.
     *
     * @param comp comparator defining the order of keys in the priority queue
     */
    public HeapPriorityQueue(Comparator<K> comp) {
        super(comp);
    }

    /**
     * Creates a priority queue initialized with the respective key-value pairs. The
     * two arrays given will be paired element-by-element. They are presumed to have
     * the same length. (If not, entries will be created only up to the length of
     * the shorter of the arrays)
     *
     * @param keys   an array of the initial keys for the priority queue
     * @param values an array of the initial values for the priority queue
     */
    public HeapPriorityQueue(K[] keys, V[] values) {
        super();
        int n = Math.min(keys.length, values.length);

        for (int i = 0; i < n; i++) {
            heap.add(new PQEntry<>(keys[i], values[i]));
        }

        heapify();
    }

    // protected utilities
    protected int parent(int j) {
        return (j - 1) / 2;
    }

    protected int left(int j) {
        return 2 * j + 1;
    }

    protected int right(int j) {
        return 2 * j + 2;
    }

    protected boolean hasLeft(int j) {
        return left(j) < heap.size();
    }

    protected boolean hasRight(int j) {
        return right(j) < heap.size();
    }

    /**
     * Exchanges the entries at indices i and j of the array list.
     */
    protected void swap(int i, int j) {
        Entry<K, V> tmpEntry = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, tmpEntry);
    }

    /**
     * Moves the entry at index j higher, if necessary, to restore the heap
     * property.
     */
    protected void upheap(int j) {
        while (j > 0) {
            int p = parent(j);

            if (compare(heap.get(j), heap.get(p)) >= 0) {
                break;
            }

            swap(j, p);
            j = p;  // keep up until to the root 0
        }
    }

    /**
     * Moves the entry at index j lower, if necessary, to restore the heap property.
     */
    protected void downheap(int j) {
        while (hasLeft(j)) {
            int smallChildIndex = left(j);

            if (hasRight(j))  {
                if (compare(heap.get(left(j)), heap.get(right(j))) > 0) {
                    smallChildIndex = right(j);
                }
            }

            if (compare(heap.get(smallChildIndex), heap.get(j)) > 0) { break; }

            swap(j, smallChildIndex);
            j = smallChildIndex;
        }
    }

    /**
     * Performs a bottom-up construction of the heap in linear time.
     */
    protected void heapify() {
        int start = parent(heap.size() - 1);

        for (int i = start; i >= 0; i--) {
            downheap(i);
        }
    }

    // public methods

    /**
     * Returns the number of items in the priority queue.
     *
     * @return number of items
     */
    @Override
    public int size() {
        return heap.size();
    }

    /**
     * Returns (but does not remove) an entry with minimal key.
     *
     * @return entry having a minimal key (or null if empty)
     */
    @Override
    public Entry<K, V> min() {
        return heap.get(0);
    }

    /**
     * Inserts a key-value pair and return the entry created.
     *
     * @param key   the key of the new entry
     * @param value the associated value of the new entry
     * @return the entry storing the new key-value pair
     * @throws IllegalArgumentException if the key is unacceptable for this queue
     */
    @Override
    public Entry<K, V> insert(K key, V value) throws IllegalArgumentException {
        checkKey(key);

        Entry<K, V> newest = new PQEntry<>(key, value);
        heap.add(newest);
        upheap(heap.size() - 1);
        return newest;
    }

    /**
     * Removes and returns an entry with minimal key.
     *
     * @return the removed entry (or null if empty)
     */
    @Override
    public Entry<K, V> removeMin() {
        if (heap.isEmpty()) return null;

        Entry<K, V> removed = heap.get(0);

        swap(0, heap.size() - 1);
        heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) { downheap(0); }

        return removed;
    }

    public String toString() {
        return heap.toString();
    }

    // Q6:
    public void pqSort(Integer[] data) {
        HeapPriorityQueue<Integer, Integer> pq = new HeapPriorityQueue<>();

        for (Integer x : data) {
            pq.insert(x, x);
        }

        int j = 0;
        while (!pq.isEmpty()) {
            Entry<Integer, Integer> e = pq.removeMin();
            data[j++] = e.getKey();
        }
    }

    // Q7:
    public void heapSort(Integer[] data) {
        int n = data.length;
        // build min-heap
        heapifyArray(data, n);
        // repeatedly move min to the end
        for (int i = n - 1; i > 0; i--) {
            swapArray(data, 0, i);
            downheapArray(data, 0, i);
        }
    }

    private static void heapifyArray(Integer[] data, int size) {
        int start = (size - 2) / 2;

        for (int j = start; j >= 0; j--) {
            downheapArray(data, j, size);
        }
    }

    private static void downheapArray(Integer[] data, int j, int size) {
        while (true) {
            int left = 2 * j + 1;
            int right = 2 * j + 2;

            int smallChild = j;

            if (left < size && data[left] < data[smallChild]) {
                smallChild = left;
            }

            if (right < size && data[right] < data[smallChild]) {
                smallChild = right;
            }

            if (smallChild == j) {
                break;
            }

            swapArray(data, j, smallChild);
            j = smallChild;
        }
    }

    private static void swapArray(Integer[] data, int i, int j) {
        Integer temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }
    /**
     * Used for debugging purposes only
     */
    private void sanityCheck() {
        for (int j = 0; j < heap.size(); j++) {
            int left = left(j);
            int right = right(j);
            //System.out.println("-> " +left + ", " + j + ", " + right);
            Entry<K, V> e_left, e_right;
            e_left = left < heap.size() ? heap.get(left) : null;
            e_right = right < heap.size() ? heap.get(right) : null;
            if (left < heap.size() && compare(heap.get(left), heap.get(j)) < 0) {
                System.out.println("Invalid left child relationship");
                System.out.println("=> " + e_left + ", " + heap.get(j) + ", " + e_right);
            }
            if (right < heap.size() && compare(heap.get(right), heap.get(j)) < 0) {
                System.out.println("Invalid right child relationship");
                System.out.println("=> " + e_left + ", " + heap.get(j) + ", " + e_right);
            }
        }
    }

    public static void main(String[] args) {
        Integer[] rands = new Integer[]{35, 26, 15, 24, 33, 4, 12, 1, 23, 21, 2, 5};
        HeapPriorityQueue<Integer, Integer> pq = new HeapPriorityQueue<>(rands, rands);

        System.out.println("elements: " + rands);
        System.out.println("after adding elements: " + pq);

        System.out.println("min element: " + pq.min());

        pq.removeMin();
        System.out.println("after removeMin: " + pq);
        // [             1,
        //        2,            4,
        //   23,     21,      5, 12,
        // 24, 26, 35, 33, 15]

        // Q5(d): Method 1: using insert O(n log n)
        HeapPriorityQueue<Integer, Integer> pq1 = new HeapPriorityQueue<>();
        for (Integer x : rands) {
            pq1.insert(x, x);
        }
        System.out.println("\nHeap built using insert():");
        System.out.println(pq1);

        // Method 2: using bottom-up O(n)
        HeapPriorityQueue<Integer, Integer> pq2 = new HeapPriorityQueue<>(rands, rands);
        System.out.println("\nHeap built using bottom-up");
        System.out.println(pq2);

//        // Q6
//        Integer[] data = {7,3,9,1,5,2,8,4,6};
//
//        System.out.println("\nBefore PQSort:");
//        System.out.println(Arrays.toString(data));
//
//        pqSort(data);
//
//        System.out.println("After PQSort:");
//        System.out.println(Arrays.toString(data));
//
//
//        // Q7
//        Integer[] data2 = {7, 3, 9, 1, 5, 2, 8, 4, 6};
//
//        System.out.println("\nBefore HeapSort:");
//        System.out.println(Arrays.toString(data2));
//
//        heapSort(data2);
//
//        System.out.println("After HeapSort:");
//        System.out.println(Arrays.toString(data2));
    }



}
