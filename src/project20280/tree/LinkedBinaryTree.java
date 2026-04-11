package project20280.tree;

import project20280.interfaces.Position;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
// import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Concrete implementation of a binary tree using a node-based, linked
 * structure.
 */
public class LinkedBinaryTree<E extends Comparable<E>> extends AbstractBinaryTree<E> {

    private static long blackhole = 0;

    static java.util.Random rnd = new java.util.Random();
    /**
     * The root of the binary tree
     */
    protected Node<E> root = null; // root of the tree

    // LinkedBinaryTree instance variables
    /**
     * The number of nodes in the binary tree
     */
    private int size = 0; // number of nodes in the tree

    /**
     * Constructs an empty binary tree.
     */
    public LinkedBinaryTree() {
    } // constructs an empty binary tree

    private int maxDiameter;

    // constructor

    public static LinkedBinaryTree<Integer> makeRandom(int n) {
        LinkedBinaryTree<Integer> bt = new LinkedBinaryTree<>();
        bt.root = randomTree(null, 1, n);
        return bt;
    }

    // nonpublic utility

    public static <T extends Integer> Node<T> randomTree(Node<T> parent, Integer first, Integer last) {
        if (first > last) return null;
        else {
            Integer treeSize = last - first + 1;
            Integer leftCount = rnd.nextInt(treeSize);
            Integer rightCount = treeSize - leftCount - 1;
            Node<T> root = new Node<T>((T) ((Integer) (first + leftCount)), parent, null, null);
            root.setLeft(randomTree(root, first, first + leftCount - 1));
            root.setRight(randomTree(root, first + leftCount + 1, last));
            return root;
        }
    }

    // accessor methods (not already implemented in AbstractBinaryTree)

    public static void main(String [] args) {
        // Wk 5 Q2 test
//        LinkedBinaryTree<String> bt = new LinkedBinaryTree<>();
//        String[] arr = { "A", "B", "C", "D", "E", null, "F", null, null, "G", "H", null, null, null, null };
//        bt.createLevelOrder(arr);
//        System.out.println(bt.toBinaryTreeString());
//
//        // Wk 5 Q3 Test
//        Integer [] inorder= {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
//        Integer [] preorder= {18, 2, 1, 14, 13, 12, 4, 3, 9, 6, 5, 8, 7, 10, 11, 15, 16, 17, 28, 23, 19, 22, 20, 21, 24, 27, 26, 25, 29, 30};
//        LinkedBinaryTree <Integer > bt2 = new LinkedBinaryTree<>();
//        bt2.construct(inorder , preorder);
//        System.out.println(bt2.toBinaryTreeString());

        // Wk5 Q6
//        try {
//            FileWriter writer = new FileWriter("Wk5_Q6_result.csv");
//            writer.write("n,averageHeight\n");
//
//            int trials = 100;
//
//            for (int n = 50; n <= 5000; n += 50) {
//
//                double totalHeight = 0;
//
//                for (int i = 0; i < trials; i++) {
//                    LinkedBinaryTree<Integer> bt3 = LinkedBinaryTree.makeRandom(n);
//
//                    totalHeight += bt3.height();
//                }
//
//                double averageHeight = totalHeight / trials;
//
//                writer.write(n + ", " + averageHeight + "\n");
//            }
//            writer.close();
//            System.out.println("CSV file created");
//        } catch(IOException e) {
//            e.printStackTrace();
//        }

        // Wk6 Q9
        LinkedBinaryTree<String> bt = new LinkedBinaryTree<>();
        String[] arr = { "A", "B", "C", "D", "E", null, "F", null, null, "G", "H", null, null, null, null };
        bt.createLevelOrder(arr);
        System.out.println(bt.toBinaryTreeString());

        System.out.println(bt.printAllLeafNodes());



        // Wk6 Q10
        // inorderExperiment();
    }


    /**
     * Factory function to create a new node storing element e.
     */
    protected Node<E> createNode(E e, Node<E> parent, Node<E> left, Node<E> right) {
        return new Node<E>(e, parent, left, right);
    }

    /**
     * Verifies that a Position belongs to the appropriate class, and is not one
     * that has been previously removed. Note that our current implementation does
     * not actually verify that the position belongs to this particular list
     * instance.
     *
     * @param p a Position (that should belong to this tree)
     * @return the underlying Node instance for the position
     * @throws IllegalArgumentException if an invalid position is detected
     */
    protected Node<E> validate(Position<E> p) throws IllegalArgumentException {
        if (!(p instanceof Node)) throw new IllegalArgumentException("Not valid position type");
        Node<E> node = (Node<E>) p; // safe cast
        if (node.getParent() == node) // our convention for defunct node
            throw new IllegalArgumentException("p is no longer in the tree");
        return node;
    }

    /**
     * Returns the number of nodes in the tree.
     *
     * @return number of nodes in the tree
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns the root Position of the tree (or null if tree is empty).
     *
     * @return root Position of the tree (or null if tree is empty)
     */
    @Override
    public Position<E> root() {
        return root;
    }

    // update methods supported by this class

    /**
     * Returns the Position of p's parent (or null if p is root).
     *
     * @param p A valid Position within the tree
     * @return Position of p's parent (or null if p is root)
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     */
    @Override
    public Position<E> parent(Position<E> p) throws IllegalArgumentException {
        return ((Node<E>) p).getParent();
    }

    /**
     * Returns the Position of p's left child (or null if no child exists).
     *
     * @param p A valid Position within the tree
     * @return the Position of the left child (or null if no child exists)
     * @throws IllegalArgumentException if p is not a valid Position for this tree
     */
    @Override
    public Position<E> left(Position<E> p) throws IllegalArgumentException {
        return ((Node<E>) p).getLeft();
    }

    /**
     * Returns the Position of p's right child (or null if no child exists).
     *
     * @param p A valid Position within the tree
     * @return the Position of the right child (or null if no child exists)
     * @throws IllegalArgumentException if p is not a valid Position for this tree
     */
    @Override
    public Position<E> right(Position<E> p) throws IllegalArgumentException {
        return ((Node<E>) p).getRight();
    }

    /**
     * Places element e at the root of an empty tree and returns its new Position.
     *
     * @param e the new element
     * @return the Position of the new element
     * @throws IllegalStateException if the tree is not empty
     */
    public Position<E> addRoot(E e) throws IllegalStateException {
        if (!isEmpty()) {
            throw new IllegalStateException("Tree is not empty");
        }
        root = createNode(e, null, null, null);
        size = 1;
        return root;
    }

    public void insert(E e) {
        if (root == null) {
            addRoot(e);
        } else {
            root = addRecursive(root, e);
        }

    }

    // recursively add Nodes to binary tree in proper position
    private Node<E> addRecursive(Node<E> p, E e) {
        if (p == null) {
            size++;
            return createNode(e, null, null, null);
        }
        int cmp = e.compareTo(p.getElement());

        if (cmp < 0) {
            Node<E> left = addRecursive(p.getLeft(), e);
            p.setLeft(left);
            left.setParent(p);
        } else if (cmp > 0) {
            Node<E> right = addRecursive(p.getRight(), e);
            p.setRight(right);
            right.setParent(p);
        }
        return p;
    }

    /**
     * Creates a new left child of Position p storing element e and returns its
     * Position.
     *
     * @param p the Position to the left of which the new element is inserted
     * @param e the new element
     * @return the Position of the new element
     * @throws IllegalArgumentException if p is not a valid Position for this tree
     * @throws IllegalArgumentException if p already has a left child
     */
    public Position<E> addLeft(Position<E> p, E e) throws IllegalArgumentException {
        Node<E> prt = validate(p);
        if (prt.getLeft() != null) {
            throw new IllegalArgumentException("Left child exists");
        }

        Node<E> child = createNode(e, prt, null, null);
        prt.setLeft(child);
        size++;
        return child;
    }

    /**
     * Creates a new right child of Position p storing element e and returns its
     * Position.
     *
     * @param p the Position to the right of which the new element is inserted
     * @param e the new element
     * @return the Position of the new element
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     * @throws IllegalArgumentException if p already has a right child
     */
    public Position<E> addRight(Position<E> p, E e) throws IllegalArgumentException {
        Node<E> prt =  validate(p);
        if (prt.getRight() != null) {
            throw new IllegalArgumentException("Right child exists");
        }

        Node<E> child = createNode(e, prt, null, null);
        prt.setRight(child);
        size++;
        return child;
    }

    /**
     * Replaces the element at Position p with element e and returns the replaced
     * element.
     *
     * @param p the relevant Position
     * @param e the new element
     * @return the replaced element
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     */
    public E set(Position<E> p, E e) throws IllegalArgumentException {
        Node<E> node = validate(p);

        E replaced = node.getElement();
        node.setElement(e);
        return replaced;
    }

    /**
     * Attaches trees t1 and t2, respectively, as the left and right subtree of the
     * leaf Position p. As a side effect, t1 and t2 are set to empty trees.
     *
     * @param p  a leaf of the tree
     * @param t1 an independent tree whose structure becomes the left child of p
     * @param t2 an independent tree whose structure becomes the right child of p
     * @throws IllegalArgumentException if p is not a valid Position for this tree
     * @throws IllegalArgumentException if p is not a leaf
     */
    public void attach(Position<E> p, LinkedBinaryTree<E> t1, LinkedBinaryTree<E> t2) throws IllegalArgumentException {
        Node<E> node = validate(p);
        if (!isExternal(p)) {
            throw new IllegalArgumentException("p must be leaf");
        }
        size += t1.size() + t2.size();

        if (!t1.isEmpty()) {
            t1.root.setParent(node);
            node.setLeft(t1.root);
            t1.root = null;
            t1.size = 0;
        }

        if (!t2.isEmpty()) {
            t2.root.setParent(node);
            node.setRight(t2.root);
            t2.root = null;
            t2.size = 0;
        }
    }

    /**
     * Removes the node at Position p and replaces it with its child, if any.
     *
     * @param p the relevant Position
     * @return element that was removed
     * @throws IllegalArgumentException if p is not a valid Position for this tree.
     * @throws IllegalArgumentException if p has two children.
     */
    public E remove(Position<E> p) throws IllegalArgumentException {
        Node<E> node = validate(p);
        if (numChildren(p) == 2) {
            throw new IllegalArgumentException("p has two children");
        }

        Node<E> child;
        if (node.getLeft() != null) {
            child = node.getLeft();
        } else {
            child = node.getRight();
        }

        if (child != null) {
            child.setParent(node.getParent());
        }

        if (node == root) {
            root = child;
        } else {
            Node<E> parent = node.getParent();
            if (node == parent.getLeft()) {
                parent.setLeft(child);
            } else {
                parent.setRight(child);
            }
        }
        size--;
        node.setParent(node);
        return node.getElement();
    }

    public String toString() {
        return positions().toString();
    }

    public void createLevelOrder(ArrayList<E> l) {
        root = createLevelOrderHelper(l, root, 0);
    }

    private Node<E> createLevelOrderHelper(java.util.ArrayList<E> l, Node<E> p, int i) {
        if (i >= l.size() || l.get(i) == null)
            return null;

        Node<E> node = createNode(l.get(i), null, null, null);
        size++;

        Node<E> leftChild = createLevelOrderHelper(l, node, 2*i + 1);
        if (leftChild != null) {
            leftChild.setParent(node);
            node.setLeft(leftChild);
        }

        Node<E> rightChild = createLevelOrderHelper(l, node, 2*i + 2);
        if (rightChild != null) {
            rightChild.setParent(node);
            node.setRight(rightChild);
        }

        return node;
    }

    public void createLevelOrder(E[] arr) {
        root = createLevelOrderHelper(arr, root, 0);
    }

    private Node<E> createLevelOrderHelper(E[] arr, Node<E> p, int i) {
        if (i >= arr.length || arr[i] == null) {
            return null;
        }

        Node<E> node = createNode(arr[i], null, null, null);
        size++;

        Node<E> leftChild = createLevelOrderHelper(arr, node, 2*i + 1);
        if (leftChild != null) {
            leftChild.setParent(node);
            node.setLeft(leftChild);
        }

        Node<E> rightChild = createLevelOrderHelper(arr, node, 2*i + 2);
        if (rightChild != null) {
            rightChild.setParent(node);
            node.setRight(rightChild);
        }
        return node;
    }

    // Q2 - Write a recursive function to count the number of external nodes in the binary tree
    private int countExternalHelper(Position<E> p) {
        if (isExternal(p)) return 1;
        int total = 0;

        for (Position<E> child : children(p)) {
            total += countExternalHelper(child);
        }
        return total;
    }

    public int countExternal() {
        if (isEmpty()) return 0;
        return countExternalHelper(root);
    }

    public String toBinaryTreeString() {
        BinaryTreePrinter<E> btp = new BinaryTreePrinter<>(this);
        return btp.print();
    }

    // Q5 - Counts the total number of descendants of a node in a binary tree
    public int countDescendants(Position<E> p) {
        int total = 0;

        for (Position<E> child : children(p)) {
            total += 1;
            total += countDescendants(child);
        }

        return total;
    }


    // Wk 5 Q3: Takes a list of both inorder and preorder nodes to contruct a unique tree
    public void construct(E[] inorder, E[] preorder) {
        root = null;
        size = 0;
        if (inorder == null || preorder == null) return;
        if (inorder.length == 0) return;

        root = constructRecursive(
                inorder, preorder,
                0, inorder.length - 1,
                0, preorder.length - 1,
                null
        );
    }

    private Node<E> constructRecursive(
            E[] inorder, E[] preorder,
            int inStart, int inEnd,
            int preStart, int preEnd,
            Node<E> parent
    ) {
        if (inStart > inEnd || preStart > preEnd) return null;

        E rootVal = preorder[preStart];

        Node<E> rootNode = createNode(rootVal, parent, null, null);
        size++;


        int rootIndex = -1;
        for (int i = inStart; i <= inEnd; i++) {
            if (inorder[i].equals(rootVal)) {
                rootIndex = i;
                break;
            }
        }

        int leftSize = rootIndex - inStart;

        rootNode.setLeft(
                constructRecursive(
                        inorder, preorder,
                        inStart, rootIndex-1,
                        preStart+1, preStart+leftSize,
                        rootNode
                )
        );

        rootNode.setRight(
                constructRecursive(
                        inorder, preorder,
                        rootIndex+1, inEnd,
                        preStart+leftSize+1, preEnd,
                        rootNode
                )
        );

        return rootNode;
    }


    // Wk5 Q4: print all root-to-leaf paths in any order
    public ArrayList<ArrayList<E>> rootToLeafPaths() {
        ArrayList<ArrayList<E>> result = new ArrayList<>();

        if (root == null) return result;

        ArrayList<E> currentPath = new ArrayList<>();
        rootToLeafRecursive(root, currentPath, result);

        return result;
    }

    private void rootToLeafRecursive(
            Node<E> node,
            ArrayList<E> currentPath,
            ArrayList<ArrayList<E>> result
    ) {
        if (node == null) return;

        currentPath.add(node.getElement());

        if (node.getLeft() == null && node.getRight() == null) {
            result.add(new ArrayList<>(currentPath));
        } else {
            rootToLeafRecursive(node.getLeft(), currentPath, result);
            rootToLeafRecursive(node.getRight(), currentPath, result);
        }

        currentPath.remove(currentPath.size() - 1);
    }


    // Wk 5 Q5: write a method to print the diameter of the binary tree
    public int diameter() {
        maxDiameter = 0;
        diameterRecursive(root);
        return maxDiameter;
    }

    private int diameterRecursive(Node<E> node) {
        if (node == null) return 0;

        int leftMost = diameterRecursive(node.getLeft());
        int rightMost = diameterRecursive(node.getRight());

        int diameterThroughNode = leftMost + rightMost + 1;

        maxDiameter = Math.max(maxDiameter, diameterThroughNode);

        return Math.max(leftMost, rightMost) + 1;
    }


    // Wk6: Q9 Write a method which prints all the leaf nodes in order from left to right
    private void printAllLeafNodes(Node<E> node, StringBuilder sb) {
        if (node == null) return;
        if (node.getLeft() == null && node.getRight() == null) {
            sb.append(node.getElement());
            return;
        }

        printAllLeafNodes(node.getLeft(), sb);
        printAllLeafNodes(node.getRight(), sb);
    }

    public String printAllLeafNodes() {
        StringBuilder sb = new StringBuilder("[");
        printAllLeafNodes(root, sb);
        sb.append("]");
        return sb.toString();
    }


    // Wk 6: Q10 The complexity T(n) of the inorder method
    public static void inorderExperiment() {
        int[] ns = {10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000};

        int warmupRuns = 5;
        int trials = 30;

        String fileName = "Wk6_Q10_inorder_times.csv";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("n,avgTimeNs\n");

            for (int n : ns) {
                LinkedBinaryTree<Integer> bt = LinkedBinaryTree.makeRandom(n);

                // warm-up
                for (int i = 0; i < warmupRuns; i++) {
                    blackhole += bt.inorderSum(bt.root);
                }

                long total = 0;
                for (int t = 0; t < trials; t++) {
                    long start = System.nanoTime();
                    blackhole += bt.inorderSum(bt.root);
                    long end = System.nanoTime();
                    total += (end - start);
                }

                long avg = total / trials;
                writer.write(n + "," + avg + "\n");
                System.out.println("n=" + n + " avg(ns)=" + avg);
            }

            System.out.println("CSV created: " + fileName);
            System.out.println("blackhole=" + blackhole);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long inorderSum(Node<E> node) {
        if (node == null) return 0;
        long left = inorderSum(node.getLeft());
        long self = node.getElement().hashCode();
        long right = inorderSum(node.getRight());
        return left + self + right;
    }


    /**
     * Nested static class for a binary tree node.
     */
    public static class Node<E> implements Position<E> {
        private E element;
        private Node<E> left, right, parent;

        public Node(E e, Node<E> p, Node<E> l, Node<E> r) {
            element = e;
            left = l;
            right = r;
            parent = p;
        }

        // accessor
        public E getElement() {
            return element;
        }

        // modifiers
        public void setElement(E e) {
            element = e;
        }

        public Node<E> getLeft() {
            return left;
        }

        public void setLeft(Node<E> n) {
            left = n;
        }

        public Node<E> getRight() {
            return right;
        }

        public void setRight(Node<E> n) {
            right = n;
        }

        public Node<E> getParent() {
            return parent;
        }

        public void setParent(Node<E> n) {
            parent = n;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (element == null) {
                sb.append("\u29B0");
            } else {
                sb.append(element);
            }
            return sb.toString();
        }
    }
}
