package com.java.trie;

import org.junit.jupiter.api.*;

import java.io.*;

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
    void addPrefixStrings() {
        boolean result1 = trie.add("One");
        boolean result2 = trie.add("On");
        assertTrue(result1);
        assertTrue(result2);
    }

    @Test
    void addEmptyStrings() {
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
    void sizeEmptyString() {
        trie.add("");
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
    void sizePrefixStrings() {
        trie.add("");
        trie.add("o");
        trie.add("on");
        trie.add("one");
        assertEquals(4, trie.size());
    }

    @Test
    void removeEmpty() {
        trie.remove("hello");
        assertEquals(0, trie.size());
    }

    @Test
    void removeNullString() {
        assertThrows(IllegalArgumentException.class,
                () -> trie.remove(null),
                "String should not be null.");
    }

    @Test
    void removeExistingString() {
        trie.add("one");
        trie.add("two");
        trie.add("three");
        trie.remove("two");
        assertEquals(2, trie.size());
        assertTrue(trie.contains("one"));
        assertFalse(trie.contains("two"));
        assertTrue(trie.contains("three"));
    }

    @Test
    void removeNonExistingString() {
        trie.add("one");
        trie.add("two");
        trie.add("three");
        trie.remove("tw");
        assertEquals(3, trie.size());
        assertTrue(trie.contains("one"));
        assertTrue(trie.contains("two"));
        assertTrue(trie.contains("three"));
    }

    @Test
    void removePrefixString() {
        trie.add("one");
        trie.remove("");
        trie.remove("o");
        trie.remove("on");
        assertEquals(1, trie.size());
        assertTrue(trie.contains("one"));
    }

    @Test
    void removeEmptyString() {
        trie.add("");
        trie.remove("");
        assertEquals(0, trie.size());
        assertFalse(trie.contains(""));
    }

    @Test
    void howManyStartsWithPrefixNull() {
        assertThrows(IllegalArgumentException.class,
                () -> trie.howManyStartsWithPrefix(null),
                "Prefix should not be null.");
    }

    @Test
    void howManyStartsWithPrefixExistingString() {
        trie.add("one");
        trie.add("two");
        trie.add("twotwo");
        trie.add("three");
        assertEquals(2, trie.howManyStartsWithPrefix("two"));
    }

    @Test
    void howManyStartsWithPrefixExistingSuffix() {
        trie.add("one");
        trie.add("two");
        trie.add("three");
        assertEquals(2, trie.howManyStartsWithPrefix("t"));
    }

    @Test
    void howManyStartsWithPrefixNonExistingSuffix() {
        trie.add("one");
        trie.add("two");
        trie.add("three");
        assertEquals(0, trie.howManyStartsWithPrefix("four"));
    }

    @Test
    void serializeEmpty() {
        try (var os = new ByteArrayOutputStream()) {
            trie.serialize(os);
            try (var is = new ByteArrayInputStream(os.toByteArray())) {
                trie = new Trie();
                trie.deserialize(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(0, trie.size());
    }

    @Test
    void serialize() {
        trie.add("one");
        trie.add("two");
        trie.add("three");
        try (var os = new ByteArrayOutputStream()) {
            trie.serialize(os);
            try (var is = new ByteArrayInputStream(os.toByteArray())) {
                trie = new Trie();
                trie.deserialize(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(3, trie.size());
        assertTrue(trie.contains("one"));
        assertTrue(trie.contains("two"));
        assertTrue(trie.contains("three"));
        assertEquals(2, trie.howManyStartsWithPrefix("t"));
        assertEquals(1, trie.howManyStartsWithPrefix("tw"));
    }

}