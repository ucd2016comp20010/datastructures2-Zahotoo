package project20280.ProjectAssignmentGroup12;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project20280.interfaces.Entry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TreapTest {

    private Treap<Integer, String> treap;
    private final Integer[] sampleKeys = {35, 26, 15, 24, 33, 4, 12, 1, 23, 21, 2, 5};

    @BeforeEach
    void setUp() {
        treap = new Treap<>();
        for (Integer k : sampleKeys) {
            treap.put(k, Integer.toString(k));
        }
    }

    @Test
    void testSizeAfterInsertions() {
        assertEquals(sampleKeys.length, treap.size());
    }

    @Test
    void testSizeEmpty() {
        Treap<Integer, String> empty = new Treap<>();
        assertEquals(0, empty.size());
    }

    @Test
    void testIsEmptyTrue() {
        Treap<Integer, String> empty = new Treap<>();
        assertTrue(empty.isEmpty());
    }

    @Test
    void testIsEmptyFalse() {
        assertFalse(treap.isEmpty());
    }

    @Test
    void testPutNewKey() {
        assertNull(treap.put(100, "100"));
        assertEquals(sampleKeys.length + 1, treap.size());
        assertEquals("100", treap.get(100));
    }

    @Test
    void testPutDuplicateKeyReturnsOldValue() {
        String old = treap.put(15, "updated");
        assertEquals("15", old);
        assertEquals("updated", treap.get(15));
        assertEquals(sampleKeys.length, treap.size());
    }

    @Test
    void testGetExistingKey() {
        assertEquals("15", treap.get(15));
        assertEquals("24", treap.get(24));
        assertEquals("1", treap.get(1));
        assertEquals("35", treap.get(35));
    }

    @Test
    void testGetNonExistingKey() {
        assertNull(treap.get(999));
        assertNull(treap.get(-1));
        assertNull(treap.get(0));
    }

    @Test
    void testRemoveExistingKey() {
        assertEquals("26", treap.remove(26));
        assertEquals(sampleKeys.length - 1, treap.size());
        assertNull(treap.get(26));
    }

    @Test
    void testRemoveNonExistingKey() {
        assertNull(treap.remove(999));
        assertEquals(sampleKeys.length, treap.size());
    }

    @Test
    void testRemoveRoot() {
        Entry<Integer, String> rootEntry = treap.firstEntry();
        assertNotNull(rootEntry);
        int rootKey = rootEntry.getKey();
        treap.remove(rootKey);
        assertNull(treap.get(rootKey));
        assertTrue(treap.isHeap());
        assertTrue(treap.isBST());
    }

    @Test
    void testRemoveAllElements() {
        for (Integer k : sampleKeys) {
            treap.remove(k);
        }
        assertEquals(0, treap.size());
        assertTrue(treap.isEmpty());
    }

    @Test
    void testBSTPropertyAfterInsertions() {
        assertTrue(treap.isBST());
    }

    @Test
    void testHeapPropertyAfterInsertions() {
        assertTrue(treap.isHeap());
    }

    @Test
    void testBSTPropertyAfterRemovals() {
        treap.remove(15);
        treap.remove(26);
        treap.remove(1);
        assertTrue(treap.isBST());
    }

    @Test
    void testHeapPropertyAfterRemovals() {
        treap.remove(15);
        treap.remove(26);
        treap.remove(1);
        assertTrue(treap.isHeap());
    }

    @Test
    void testEntrySetInOrder() {
        List<Integer> keys = new ArrayList<>();
        for (Entry<Integer, String> e : treap.entrySet()) {
            keys.add(e.getKey());
        }
        assertEquals(List.of(1, 2, 4, 5, 12, 15, 21, 23, 24, 26, 33, 35), keys);
    }

    @Test
    void testKeySet() {
        List<Integer> keys = new ArrayList<>();
        treap.keySet().forEach(keys::add);
        assertEquals(List.of(1, 2, 4, 5, 12, 15, 21, 23, 24, 26, 33, 35), keys);
    }

    @Test
    void testValues() {
        List<String> vals = new ArrayList<>();
        treap.values().forEach(vals::add);
        assertEquals(List.of("1", "2", "4", "5", "12", "15", "21", "23", "24", "26", "33", "35"), vals);
    }

    @Test
    void testFirstEntry() {
        assertEquals(1, treap.firstEntry().getKey());
    }

    @Test
    void testFirstEntryEmpty() {
        Treap<Integer, String> empty = new Treap<>();
        assertNull(empty.firstEntry());
    }

    @Test
    void testLastEntry() {
        assertEquals(35, treap.lastEntry().getKey());
    }

    @Test
    void testLastEntryEmpty() {
        Treap<Integer, String> empty = new Treap<>();
        assertNull(empty.lastEntry());
    }

    @Test
    void testCeilingEntryExactMatch() {
        assertEquals(12, treap.ceilingEntry(12).getKey());
    }

    @Test
    void testCeilingEntryNoExactMatch() {
        assertEquals(12, treap.ceilingEntry(11).getKey());
    }

    @Test
    void testCeilingEntryAboveMax() {
        assertNull(treap.ceilingEntry(36));
    }

    @Test
    void testFloorEntryExactMatch() {
        assertEquals(5, treap.floorEntry(5).getKey());
    }

    @Test
    void testFloorEntryNoExactMatch() {
        assertEquals(5, treap.floorEntry(11).getKey());
    }

    @Test
    void testFloorEntryBelowMin() {
        assertNull(treap.floorEntry(0));
    }

    @Test
    void testLowerEntry() {
        assertEquals(23, treap.lowerEntry(24).getKey());
    }

    @Test
    void testLowerEntryAtMin() {
        assertNull(treap.lowerEntry(1));
    }

    @Test
    void testHigherEntry() {
        assertEquals(12, treap.higherEntry(5).getKey());
    }

    @Test
    void testHigherEntryAtMax() {
        assertNull(treap.higherEntry(35));
    }

    @Test
    void testSubMap() {
        List<Integer> keys = new ArrayList<>();
        for (Entry<Integer, String> e : treap.subMap(5, 24)) {
            keys.add(e.getKey());
        }
        assertEquals(List.of(5, 12, 15, 21, 23), keys);
    }

    @Test
    void testSubMapEmptyRange() {
        List<Integer> keys = new ArrayList<>();
        for (Entry<Integer, String> e : treap.subMap(50, 60)) {
            keys.add(e.getKey());
        }
        assertTrue(keys.isEmpty());
    }

    @Test
    void testLargeRandomInsertions() {
        Treap<Integer, Integer> big = new Treap<>();
        Random rnd = new Random(42);
        int n = 5000;
        for (int i = 0; i < n; i++) {
            big.put(rnd.nextInt(100000), i);
        }
        assertTrue(big.isBST());
        assertTrue(big.isHeap());
        assertTrue(big.size() > 0);
    }

    @Test
    void testLargeRandomInsertionsAndDeletions() {
        Treap<Integer, Integer> big = new Treap<>();
        Random rnd = new Random(42);
        List<Integer> inserted = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            int key = rnd.nextInt(10000);
            big.put(key, i);
            inserted.add(key);
        }

        for (int i = 0; i < 500; i++) {
            big.remove(inserted.get(i));
        }

        assertTrue(big.isBST());
        assertTrue(big.isHeap());
    }

    @Test
    void testSortedAscendingInsertions() {
        Treap<Integer, Integer> t = new Treap<>();
        for (int i = 0; i < 500; i++) {
            t.put(i, i);
        }
        assertEquals(500, t.size());
        assertTrue(t.isBST());
        assertTrue(t.isHeap());
        assertEquals(0, t.firstEntry().getKey());
        assertEquals(499, t.lastEntry().getKey());
    }

    @Test
    void testSortedDescendingInsertions() {
        Treap<Integer, Integer> t = new Treap<>();
        for (int i = 499; i >= 0; i--) {
            t.put(i, i);
        }
        assertEquals(500, t.size());
        assertTrue(t.isBST());
        assertTrue(t.isHeap());
    }

    @Test
    void testStringKeys() {
        Treap<String, Integer> t = new Treap<>();
        t.put("banana", 1);
        t.put("apple", 2);
        t.put("cherry", 3);
        t.put("date", 4);

        assertEquals(4, t.size());
        assertEquals(2, t.get("apple"));
        assertEquals("apple", t.firstEntry().getKey());
        assertEquals("date", t.lastEntry().getKey());
        assertTrue(t.isBST());
        assertTrue(t.isHeap());
    }
}
