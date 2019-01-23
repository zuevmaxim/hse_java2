package com.java.trie;

import java.util.HashMap;

public class Trie {
    private class Vertex {
        private HashMap<Character, Vertex> next;
        private boolean isTerminal;

        public Vertex() {
            next = new HashMap<>();
            isTerminal = false;
        }

        public Vertex getNext(Character character) {
            return next.get(character);
        }

        public void setNext(Character character, Vertex vertex) {
            next.put(character, vertex);
        }

        public void markTerminal() {
            isTerminal = true;
        }

        public boolean isTerminal() {
            return isTerminal;
        }
    }

    private Vertex root;
    private int size;

    public Trie() {
        root = new Vertex();
        size = 0;
    }

    public boolean add(String string) throws IllegalArgumentException{
        if (string == null) {
            throw new IllegalArgumentException("String should not be null.");
        }
        Vertex currentVertex = root;
        for (char c : string.toCharArray()) {
            if (currentVertex.getNext(c) == null) {
                currentVertex.setNext(c, new Vertex());
            }
            currentVertex = currentVertex.getNext(c);
        }
        boolean result = !currentVertex.isTerminal();
        if (result) {
            size++;
        }
        currentVertex.markTerminal();
        return result;
    }

    public boolean contains(String string) throws IllegalArgumentException {
        if (string == null) {
            throw new IllegalArgumentException("String should not be null.");
        }
        Vertex currentVertex = root;
        for (char c : string.toCharArray()) {
            if (currentVertex.getNext(c) == null) {
                return false;
            }
            currentVertex = currentVertex.getNext(c);
        }
        return currentVertex.isTerminal();
    }

    public int size() {
        return size;
    }
}