package com.example.list;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;

public class List<E> extends AbstractList<E> {
    private class Node<E> {
        private Node next;
        private Node prev;
        private E element;
        public Node(Node next, Node prev, E element) {
            this.next = next;
            this.prev = prev;
            this.element = element;
        }
        public void setElement(E element) {
            this.element = element;
        }
    }

    private Node head;
    private int size;

    private class ListIterator<E> implements java.util.ListIterator<E> {
        private Node<E> currentNode;
        private int index;

        public ListIterator() {
            currentNode = head;
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return currentNode.next != null;
        }

        @Override
        public E next() {
            index++;
            currentNode = currentNode.next;
            return (E) currentNode.element;
        }

        @Override
        public boolean hasPrevious() {
            return currentNode.prev != null;
        }

        @Override
        public E previous() {
            index--;
            currentNode = currentNode.prev;
            return (E) currentNode.element;
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
        public void set(E o) {
            if (o == null) {
                throw new IllegalArgumentException("Null");
            }
            currentNode.setElement(o);
        }

        @Override
        public void add(E o) {
            size++;
            var newNode = new Node(currentNode, currentNode.prev, o);
            if (hasPrevious()) {
                currentNode.prev.next = newNode;
            } else {
                head = newNode;
            }
            currentNode.prev = newNode;
        }
    }

    @Override
    public E get(int index) {
        for (var it = iterator(); it.hasNext() && it.nextIndex() <= index; ) {
            if (it.nextIndex() == index) {
                return (E) it.next();
            }
            it.next();
        }
        return null;
    }

    @Override
    public int size() {
        return size++;
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
    public ListIterator iterator() {
        return new ListIterator<>();
    }

}