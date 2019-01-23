package com.java.trie;

import java.util.HashMap;

/**
 * Trie stores a dynamic set of strings
 */
public class Trie {
    /**
     * Vertex of tree of possible suffixes of string
     */
    private class Vertex {
        /**
         * Possible next symbols of string
         */
        private HashMap<Character, Vertex> next;

        /**
         * True iff trie contains a string that ends in this vertex
         */
        private boolean isTerminal;

        /**
         * Number of terminal vertices in a subtree
         */
        private int subTrieSize;

        /**
         * Create empty vertex
         */
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

        /**
         * Set if vertex is terminal
         */
        public void setTerminal(boolean isTerminal) {
            this.isTerminal = isTerminal;
        }

        public boolean isTerminal() {
            return isTerminal;
        }

        /**
         * Increase if a new string appears
         */
        public void incSubTrieSize() {
            subTrieSize++;
        }

        /**
         * Decrease if string is deleted
         */
        public void decSubTrieSize() {
            subTrieSize--;
        }

        public int getSubTrieSize() {
            return subTrieSize;
        }
    }

    private Vertex root;
    private int size;

    /**
     * Create an empty trie
     */
    public Trie() {
        root = new Vertex();
        size = 0;
    }

    /**
     * Add new string
     * @param string string to add
     * @return true iff there had not been such string in a trie
     * @throws IllegalArgumentException if string is null
     */
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

    /**
     * Check if trie contains the string
     * @param string string to find
     * @return true iff trie contains the string
     * @throws IllegalArgumentException if string is null
     */
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

    /**
     * Trie size
     * @return number of string in the trie
     */
    public int size() {
        return size;
    }

    /**
     * Remove string from the trie
     * @param string string to delete
     * @return true iff such string had been in the trie
     * @throws IllegalArgumentException if string is null
     */
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

    /**
     * How many strings in the trie starts with such prefix
     * @throws IllegalArgumentException if prefix is null
     */
    public int howManyStartsWithPrefix(String prefix) throws IllegalArgumentException {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix should not be null.");
        }
        Vertex currentVertex = root;
        for (char c : prefix.toCharArray()) {
            if (currentVertex.getNext(c) == null){
                return 0;
            }
            currentVertex = currentVertex.getNext(c);
        }
        return currentVertex.getSubTrieSize();
    }
}