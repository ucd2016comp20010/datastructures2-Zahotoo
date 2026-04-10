package project20280.tree;

import project20280.interfaces.Entry;
import project20280.interfaces.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BSTTestModified {
    // Returns the height of the tree (0 if empty)
    static int treeHeight(TreeMap<Integer, Integer> map) {
        if (map.isEmpty()) return 0;
        return heightOf(map, map.root());
    }

    static int heightOf(TreeMap<Integer, Integer> map, Position<Entry<Integer, Integer>> p) {
        if (map.isExternal(p)) return 0;
        return 1 + Math.max(heightOf(map, map.left(p)), heightOf(map, map.right(p)));
    }

    public static void main(String[] args) {
        Random rnd = new Random();
        int n_max = 150;

        // Q3: build a 100-node tree, then do 10000 random put/remove, track average height
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        rnd.ints(1, n_max).limit(100).distinct().boxed().forEach(x -> treeMap.put(x, x));

        int n_trials = 10000;
        long totalHeight = 0;

        for (int i = 0; i < n_trials; ++i) {
            var keyset = treeMap.keySet();
            List<Integer> target = new ArrayList<>();
            keyset.forEach(target::add);

            if (treeMap.size() < n_max && rnd.nextFloat() > 0.5) {
                while (true) {
                    Integer x = rnd.nextInt(n_max);
                    if (!target.contains(x)) {
                        treeMap.put(x, x);
                        break;
                    }
                }
            } else {
                if (treeMap.size() == 0) continue;
                Integer x = target.get(rnd.nextInt(target.size()));
                treeMap.remove(x);
            }

            totalHeight += treeHeight(treeMap);
        }

        double avgHeight = (double) totalHeight / n_trials;
        int finalSize = treeMap.size();
        System.out.printf("After %d trials: avg height = %.2f, size = %d%n", n_trials, avgHeight, finalSize);
        System.out.printf("sqrt(n) = %.2f%n", Math.sqrt(finalSize));
    }
}

//  What to observe: The average height should grow roughly as O(√n) for random put/remove sequences — noticeably taller
//  than the O(log n) get from an AVL tree.