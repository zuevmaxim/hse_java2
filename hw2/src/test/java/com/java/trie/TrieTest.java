package com.java.trie;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {
    private Trie trie;

    @BeforeEach
    void init() {
        trie = new Trie();
    }

    @Test
    void addNullString() {
        assertThrows(IllegalArgumentException.class,
                () -> trie.add(null),
                "String should not be null.");
    }

    @Test
    void addOneString() {
        boolean result = trie.add("One");
        assertTrue(result);
    }

    @Test
    void addSimilarStrings() {
        boolean result1 = trie.add("One");
        boolean result2 = trie.add("One");
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    void addSuffixStrings() {
        boolean result1 = trie.add("One");
        boolean result2 = trie.add("On");
        assertTrue(result1);
        assertTrue(result2);
    }

    @Test
    void addEmptyxStrings() {
        boolean result = trie.add("");
        assertTrue(result);
    }

    @Test
    void containsNullString() {
        assertThrows(IllegalArgumentException.class,
                () -> trie.contains(null),
                "String should not be null.");
    }

    @Test
    void containsExistingString() {
        trie.add("hello");
        assertTrue(trie.contains("hello"));
    }

    @Test
    void containsNonExistingString() {
        trie.add("hello");
        assertFalse(trie.contains(""));
        assertFalse(trie.contains("h"));
        assertFalse(trie.contains("he"));
        assertFalse(trie.contains("hel"));
        assertFalse(trie.contains("hell"));
    }

    @Test
    void sizeEmpty() {
        assertEquals(0, trie.size());
    }

    @Test
    void sizeOneString() {
        trie.add("one");
        assertEquals(1, trie.size());
    }

    @Test
    void sizeManyStrings() {
        trie.add("one");
        trie.add("two");
        trie.add("three");
        assertEquals(3, trie.size());
    }

    @Test
    void sizeSimilarStrings() {
        trie.add("one");
        trie.add("one");
        trie.add("one");
        assertEquals(1, trie.size());
    }

    @Test
    void sizeSuffixStrings() {
        trie.add("");
        trie.add("o");
        trie.add("on");
        trie.add("one");
        assertEquals(4, trie.size());
    }
}