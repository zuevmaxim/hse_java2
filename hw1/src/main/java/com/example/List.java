package com.example;

public class List {
    private Node head;
    private int size;

    private class Node {
        public Node next;
        public Node prev;
        public Elem elem;
        public Node(Node nxt, Node prv, Elem elm) {
            next = nxt;
            prev = prv;
            elem = elm;
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
        Node cur = head;
        while (cur != null) {
            if (cur.elem.getKey().equals(key)) {
                return cur;
            }
            cur = cur.next;
        }
        return null;
    }

    /**
     * Find element in list by key
     * @param key which element should be found
     * @return element which key equals given parameter
     */
    public Elem find(String key) {
        Node node = findNode(key);
        if (node == null) {
            return null;
        }
        return node.elem;
    }

    /**
     * Insert new element to the head of the list
     * @param elem which element should be inserted
     */
    public void insert(Elem elem) {
        ++size;
        Node newNode = new Node(head, null, elem);
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
    public Elem[] toArray() {
        Elem[] a = new Elem[size];
        Node cur = head;
        int i = 0;
        while (cur != null) {
            a[i++] = cur.elem;
            cur = cur.next;
        }
        return a;
    }
}
