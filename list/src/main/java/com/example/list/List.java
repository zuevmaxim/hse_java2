package com.example.list;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractList;

public class List<E> extends AbstractList<E> {
    private class Node<T> {
        private Node<T> next;
        private Node<T> prev;
        private T element;
        public Node(Node<T> next, Node<T> prev, T element) {
            this.next = next;
            this.prev = prev;
            this.element = element;
        }
    }

    private Node<E> head = new Node<>(null, null, null);
    private int size;

    private class ListIterator implements java.util.ListIterator<E> {
        private Node<E> currentNode;
        private int index;

        public ListIterator() {
            currentNode = head;
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public E next() {
            index++;
            E value = currentNode.element;
            currentNode = currentNode.next;
            return value;
        }

        @Override
        public boolean hasPrevious() {
            return currentNode.prev != null;
        }

        @Override
        public E previous() {
            index--;
            currentNode = currentNode.prev;
            return currentNode.element;
        }

        @Override
        public int nextIndex() {
            return index;
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
            currentNode.element = (E) o;
        }

        @Override
        public void add(Object o) {
            size++;
            Node<E> newNode = new Node<>(currentNode, currentNode.prev, (E) o);
            if (hasPrevious()) {
                currentNode.prev.next = newNode;
            } else {
                head = newNode;
            }
            currentNode = newNode;
        }
    }

    @Override
    public E get(int index) {
        for (var it = iterator(); it.hasNext() && it.nextIndex() <= index; ) {
            if (it.nextIndex() == index) {
                return it.next();
            }
            it.next();
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public E set(int index, E element) {
        for (var it = iterator(); it.hasNext() && it.nextIndex() <= index; ) {
            if (it.nextIndex() == index) {
                var e = (E) it.next();
                it.set(element);
                return e;
            }
            it.next();
        }
        return null;
    }

    public boolean add(E e) {
        iterator().add(e);
        return true;
    }

    @Override
    @NotNull
    public ListIterator iterator() {
        return new ListIterator();
    }

}