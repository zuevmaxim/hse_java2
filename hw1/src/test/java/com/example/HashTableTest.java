package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    @Test
    void sizeEmpty() {
        HashTable h = new HashTable();
        assertEquals(0, h.size());
    }

    @Test
    void size100DifferentElements() {
        HashTable h = new HashTable();
        String k = "k";
        String v = "v";
        for (int i = 0; i < 100; ++i) {
            h.put(k, v);
            k += "k";
            v += "v";
        }
        assertEquals(100, h.size());
    }

    @Test
    void size200RepeatingElements() {
        HashTable h = new HashTable();
        String k = "k";
        String v = "v";
        for (int i = 0; i < 100; ++i) {
            h.put(k, v);
            k += "k";
            v += "v";
        }
        k = "k";
        v = "v";
        for (int i = 0; i < 100; ++i) {
            h.put(k, v);
            k += "k";
            v += "v";
        }
        assertEquals(100, h.size());
    }

    @Test
    void containsEmpty() {
        HashTable h = new HashTable();
        assertFalse(h.contains("123"));
    }

    @Test
    void containsExistingElement() {
        HashTable h = new HashTable();
        h.put("my", "dog");
        assertTrue(h.contains("my"));
    }

    @Test
    void containsNonExistingElement() {
        HashTable h = new HashTable();
        h.put("my", "dog");
        assertFalse(h.contains("your"));
    }

    @Test
    void getEmpty() {
        HashTable h = new HashTable();
        assertNull(h.get("Hello"));
    }

    @Test
    void getExistingElement() {
        HashTable h = new HashTable();
        h.put("my", "dog");
        h.put("your", "cat");
        assertEquals("cat", h.get("your"));
        assertEquals("dog", h.get("my"));
    }

    @Test
    void getNonExistingElement() {
        HashTable h = new HashTable();
        h.put("my", "dog");
        h.put("your", "cat");
        assertNull(h.get("his"));
    }

    @Test
    void putOneElement() {
        HashTable h = new HashTable();
        h.put("my", "dog");
        assertEquals(1, h.size());
    }

    @Test
    void putRepeatingElements() {
        HashTable h = new HashTable();
        for (int i = 0; i < 10; ++i) {
            h.put("my", "dog");
        }
        assertEquals(1, h.size());
        assertEquals("dog", h.get("my"));
    }

    @Test
    void removeEmpty() {
        HashTable h = new HashTable();
        h.remove("1");
        assertEquals(0, h.size());
        assertNull(h.get("1"));
    }

    @Test
    void removeExistingElement() {
        HashTable h = new HashTable();
        h.put("1", "one");
        h.put("2", "two");
        h.put("3", "three");
        h.remove("1");
        assertEquals(2, h.size());
        assertNull(h.get("1"));
        assertEquals("two", h.get("2"));
        assertEquals("three", h.get("3"));
    }

    @Test
    void removeNonExistingElement() {
        HashTable h = new HashTable();
        h.put("1", "one");
        h.put("2", "two");
        h.put("3", "three");
        h.remove("0");
        assertEquals(3, h.size());
        assertNull(h.get("0"));
        assertEquals("one", h.get("1"));
        assertEquals("two", h.get("2"));
        assertEquals("three", h.get("3"));
    }

    @Test
    void clearEmpty() {
        HashTable h = new HashTable();
        h.clear();
        assertEquals(0, h.size());
        assertNull(h.get("smth"));
    }

    @Test
    void clear() {
        HashTable h = new HashTable();
        h.put("1", "one");
        h.put("2", "two");
        h.put("3", "three");
        h.put("4", "four");
        h.put("5", "five");

        h.clear();
        assertEquals(0, h.size());
        assertNull(h.get("1"));
        assertNull(h.get("2"));
        assertNull(h.get("3"));
        assertNull(h.get("4"));
        assertNull(h.get("5"));
    }
}