package project20280.stacksqueues;

import project20280.interfaces.Queue;
import project20280.list.CircularlyLinkedList;
/**
 * Realization of a circular FIFO queue as an adaptation of a
 * CircularlyLinkedList. This provides one additional method not part of the
 * general Queue interface. A call to rotate() is a more efficient simulation of
 * the combination enqueue(dequeue()). All operations are performed in constant
 * time.
 */

public class LinkedCircularQueue<E> implements Queue<E> {

    private CircularlyLinkedList<E> cll;

    public LinkedCircularQueue() {
        cll = new CircularlyLinkedList<>();
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    @Override
    public int size() {
        return cll.size();
    }

    @Override
    public boolean isEmpty() {
        return cll.isEmpty();
    }

    @Override
    public void enqueue(E e) {
        // TODO Auto-generated method stub
        cll.addLast(e);
    }

    @Override
    public E first() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public E dequeue() {
        // TODO Auto-generated method stub
        if (isEmpty()) {
            return null;
        }
        return cll.removeFirst();
    }

}
