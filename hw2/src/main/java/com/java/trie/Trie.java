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
    }
}