package com.example.list;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

public class List<E> implements java.util.List {
    private class Node {
        private Node next;
        private Node prev;
        private E element;
        private Node(Node next, Node prev, E element) {
            this.next = next;
            this.prev = prev;
            this.element = element;
        }
    }

    private class ListIterator<E> implements java.util.ListIterator {
        private Node currentNode;
        private int index;

        @Override
        public boolean hasNext() {
            return currentNode.next != null;
        }

        @Override
        public Object next() {
            index++;
            currentNode = currentNode.next;
            return currentNode;
        }

        @Override
        public boolean hasPrevious() {
            return currentNode.prev != null;
        }

        @Override
        public Object previous() {
            index--;
            currentNode = currentNode.prev;
            return currentNode;
        }

        @Override
        public int nextIndex() {
            return index + 1;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            --size;
            if (hasPrevious()) {
                currentNode.prev.next = currentNode.next;
            } else {
                head = currentNode.next;
            }
            if (hasNext()) {
                currentNode.next.prev = currentNode.prev;
            }
            currentNode = currentNode.next;
        }

        @Override
        public void set(Object o) {
            if (o == null) {
                throw new IllegalArgumentException("Null");
            }
            currentNode.element = o;
        }

        @Override
        public void add(Object o) {

        }
    }

    private Node head = null;
    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @NotNull
    @Override
    public Iterator iterator() {
        return null;
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public boolean add(Object o) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection c) {
        return false;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public Object get(int index) {
        return null;
    }

    @Override
    public Object set(int index, Object element) {
        return null;
    }

    @Override
    public void add(int index, Object element) {

    }

    @Override
    public Object remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @NotNull
    @Override
    public ListIterator listIterator() {
        return null;
    }

    @NotNull
    @Override
    public ListIterator listIterator(int index) {
        return null;
    }

    @NotNull
    @Override
    public java.util.List subList(int fromIndex, int toIndex) {
        return null;
    }

    @Override
    public boolean retainAll(@NotNull Collection c) {
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection c) {
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection c) {
        return false;
    }

    @NotNull
    @Override
    public Object[] toArray(@NotNull Object[] a) {
        return new Object[0];
    }
}
