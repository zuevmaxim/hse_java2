import java.util.AbstractSet;
import java.util.Iterator;

public class BSTSet<E> extends AbstractSet<E> implements MyTreeSet<E> {
    private static class Node<E> {
        private E element;
        private Node<E> left;
        private Node<E> right;
        private Node<E> parent;
        private int size;
        private int height;

        public Node(E element) {
            this.element = element;
        }
    }

    @Override
    public Iterator<E> descendingIterator() {
        return null;
    }

    @Override
    public MyTreeSet<E> descendingSet() {
        return null;
    }

    @Override
    public E first() {
        return null;
    }

    @Override
    public E last() {
        return null;
    }

    @Override
    public E lower(E e) {
        return null;
    }

    @Override
    public E floor(E e) {
        return null;
    }

    @Override
    public E ceiling(E e) {
        return null;
    }

    @Override
    public E higher(E e) {
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
