package com.java.trie;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Trie stores a dynamic set of strings
 */
public class Trie implements Serializable {
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

        /**
         * Check if vertex is terminal
         */
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

        /**
         * Initialize subTreeSize
         */
        private void setSubTrieSize(int subTrieSize) {
            this.subTrieSize = subTrieSize;
        }

        public int getSubTrieSize() {
            return subTrieSize;
        }

        private Set<Map.Entry<Character, Vertex>> getNexts() {
            return next.entrySet();
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
        if (string == null) {
            throw new IllegalArgumentException("String should not be null.");
        }
        if (contains(string)) {
            return false;
        }
        size++;
        Vertex currentVertex = root;
        for (char currentCharacter : string.toCharArray()) {
            if (currentVertex.getNext(currentCharacter) == null) {
                currentVertex.setNext(currentCharacter, new Vertex());
            }
            currentVertex.incSubTrieSize();
            currentVertex = currentVertex.getNext(currentCharacter);
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
        for (char currentCharacter : string.toCharArray()) {
            if (currentVertex.getNext(currentCharacter) == null) {
                return false;
            }
            currentVertex = currentVertex.getNext(currentCharacter);
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
    public boolean remove(String string) throws IllegalArgumentException {
        if (string == null) {
            throw new IllegalArgumentException("String should not be null.");
        }
        if (!contains(string)) {
            return false;
        }
        --size;
        Vertex currentVertex = root;
        for (char currentCharacter : string.toCharArray()) {
            if (currentVertex.getNext(currentCharacter).getSubTrieSize() == 1) {
                currentVertex.deleteNext(currentCharacter);
                currentVertex.decSubTrieSize();
                return true;
            }
            currentVertex.decSubTrieSize();
            currentVertex = currentVertex.getNext(currentCharacter);
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
        for (char currentCharacter : prefix.toCharArray()) {
            if (currentVertex.getNext(currentCharacter) == null) {
                return 0;
            }
            currentVertex = currentVertex.getNext(currentCharacter);
        }
        return currentVertex.getSubTrieSize();
    }

    /**
     * Subclass for serializing and deserializing
     */
    private class TrieSerializer {
        /**
         * Full description of Vertex for (de)serializing
         */
        private class VertexId {
            /**
             * Parent's number in dfs path
             */
            private int parentIndex;
            private char parentChar;
            private boolean isTerminal;

            /**
             * Construct VertexId
             */
            public VertexId(int parentIndex, char parentChar, boolean isTerminal) {
                this.parentIndex = parentIndex;
                this.parentChar = parentChar;
                this.isTerminal = isTerminal;
            }
        }

        private ArrayList<VertexId> vertexIds;

        /**
         * Construct serializer
         */
        public TrieSerializer() {
            vertexIds = new ArrayList<>();
        }

        /**
         * Serialize trie to the output stream in format:
         * (int)N -- number of vertices, -1, 0, (int) if root is terminal,
         * for each vertex 1..N-1 : (int)parent index, (int)parent char, (int) if vertex is terminal
         * @param out output stream
         * @throws IOException  if an I/O error occurs
         */
        public void serialize(OutputStream out) throws IOException {
            vertexIds.add(new VertexId(-1, (char) 0, root.isTerminal()));
            dfs(root, 0);
            out.write(vertexIds.size());
            for (VertexId vertexId : vertexIds) {
                out.write(vertexId.parentIndex);
                out.write((int)vertexId.parentChar);
                out.write(vertexId.isTerminal ? 1 : 0);
            }
        }

        /**
         * Deserialize trie from the input stream
         * @param in input stream
         * @throws IOException if there no enough data or other I/O error occurs
         */
        public void deserialize(InputStream in) throws IOException {
            int vertexNumber = in.read();
            if (vertexNumber == -1) {
                throw new EOFException("Unexpected end of input stream");
            }
            for (int i = 0; i < vertexNumber; i++) {
                int parentIndex = in.read();
                int parentChar = in.read();
                int isTerminal = in.read();
                if (parentIndex == -1 || parentChar == -1 || isTerminal == -1) {
                    throw new EOFException("Unexpected end of input stream");
                }
                vertexIds.add(new VertexId(parentIndex, (char)parentChar, isTerminal == 1));
            }

            var vertices = new Vertex[vertexNumber];
            for (int i = 0; i < vertexNumber; i++) {
                vertices[i] = new Vertex();
            }

            vertices[0].setTerminal(vertexIds.get(0).isTerminal);

            size = 0;
            for (int i = 1; i < vertexNumber; i++) {
                var vertexId = vertexIds.get(i);
                vertices[vertexId.parentIndex].setNext(vertexId.parentChar, vertices[i]);
                vertices[i].setTerminal(vertexId.isTerminal);
                if (vertexId.isTerminal) {
                    size++;
                }
            }

            root = vertices[0];
            calculateSubTreeSizes(root);
        }

        /**
         * Recursively calculate the number of terminal vertices in a subtree of every vertex in the trie
         */
        private void calculateSubTreeSizes(Vertex vertex) {
            int vertexSubTreeSize = vertex.isTerminal() ? 1 : 0;
            for (var entry : vertex.next.entrySet()){
                calculateSubTreeSizes(entry.getValue());
                vertexSubTreeSize += entry.getValue().subTrieSize;
            }
            vertex.setSubTrieSize(vertexSubTreeSize);
        }

        /**
         * Find a parent recursively for every vertex in the trie
         * @param parent parent's index in vertexIds array
         */
        private void dfs(Vertex vertex, int parent) {
            var vertices = vertex.getNexts();
            for (var entry : vertices) {
                vertexIds.add(new VertexId(parent, entry.getKey(), entry.getValue().isTerminal()));
                dfs(entry.getValue(), vertexIds.size() - 1);
            }
        }

    }

    @Override
    public void serialize(OutputStream out) throws IOException {
        var trieSerializer = new TrieSerializer();
        trieSerializer.serialize(out);
    }

    @Override
    public void deserialize(InputStream in) throws IOException {
        var trieSerializer = new TrieSerializer();
        trieSerializer.deserialize(in);
    }

}