package com.java.trie;

import java.util.HashMap;

public class Trie {
    private class Vertex {
        private HashMap<Character, Vertex> next;
        private boolean isTerminal;
        private int subTrieSize;

        public Vertex() {
            next = new HashMap<>();
            isTerminal = false;
            subTrieSize = 0;
        }

        public Vertex getNext(Character character) {
            return next.get(character);
        }

        public void setNext(Character character, Vertex vertex) {
            next.put(character, vertex);
        }

        public void deleteNext(Character character) {
            next.remove(character);
        }

        public void setTerminal(boolean isTerminal) {
            this.isTerminal = isTerminal;
        }

        public boolean isTerminal() {
            return isTerminal;
        }

        public void incSubTrieSize() {
            subTrieSize++;
        }

        public void decSubTrieSize() {
            subTrieSize--;
        }

        public int getSubTrieSize() {
            return subTrieSize;
        }
    }

    private Vertex root;
    private int size;

    public Trie() {
        root = new Vertex();
        size = 0;
    }

    public boolean add(String string) throws IllegalArgumentException{
        if (contains(string)) {
            return false;
        }
        size++;
        Vertex currentVertex = root;
        for (char c : string.toCharArray()) {
            if (currentVertex.getNext(c) == null) {
                currentVertex.setNext(c, new Vertex());
            }
            currentVertex.incSubTrieSize();
            currentVertex = currentVertex.getNext(c);
        }
        currentVertex.incSubTrieSize();
        currentVertex.setTerminal(true);
        return true;
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

    public boolean remove(String string) throws IllegalArgumentException{
        if (!contains(string)) {
            return false;
        }
        --size;
        Vertex currentVertex = root;
        for (char c : string.toCharArray()) {
            if (currentVertex.getNext(c).getSubTrieSize() == 1) {
                currentVertex.deleteNext(c);
                currentVertex.decSubTrieSize();
                return true;
            }
            currentVertex.decSubTrieSize();
            currentVertex = currentVertex.getNext(c);
        }
        currentVertex.decSubTrieSize();
        currentVertex.setTerminal(false);
        return true;
    }
}