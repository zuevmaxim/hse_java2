package com.example.hashtable;


import org.jetbrains.annotations.Nullable;

/**
 * List that contains elements
 */
public class List {
    private Node head;
    private int size;

    /**
     * List node that contains one element
     */
    private class Node {
        private Node next;
        private Node prev;
        private Element element;
        private Node(Node next, Node prev, Element element) {
            this.next = next;
            this.prev = prev;
            this.element = element;
        }
    }

    /**
     * List size
     * @return list size
     */
    public int size() {
        return size;
    }

    /**
     * Construct empty list
     */
    public List() {
        head = null;
        size = 0;
    }

    /**
     * Find node in list by key
     * @param key which element should be found
     * @return node containing element
     */
    private Node findNode(String key) {
        Node currentNode = head;
        while (currentNode != null) {
            if (currentNode.element.getKey().equals(key)) {
                return currentNode;
            }
            currentNode = currentNode.next;
        }
        return null;
    }

    /**
     * Find element in list by key
     * @param key which element should be found
     * @return element which key equals given parameter
     */
    public Element find(String key) {
        Node node = findNode(key);
        if (node == null) {
            return null;
        }
        return node.element;
    }

    /**
     * Insert new element to the head of the list
     * @param element which element should be inserted
     */
    public void insert(Element element) {
        ++size;
        var newNode = new Node(head, null, element);
        if (head != null) {
            head.prev = newNode;
        }
        head = newNode;
    }

    /**
     * Remove node from list by node
     * @param node which node should be deleted
     */
    private void remove(Node node) {
        if (node == null) {
            return;
        }
        --size;
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
    }

    /**
     * Remove node from list by key
     * @param key which element should be deleted
     */
    public void remove(String key) {
        remove(findNode(key));
    }

    /**
     * Make list clean
     */
    public void clear() {
        size = 0;
        head = null;
    }

    /**
     * Transform list to array
     * @return array which contains list elements
     */
    public Element[] toArray() {
        var array = new Element[size];
        Node currentNode = head;
        int i = 0;
        while (currentNode != null) {
            array[i] = currentNode.element;
            currentNode = currentNode.next;
            i++;
        }
        return array;
    }
}
