package com.example.hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {
    private HashTable hashTable;

    @BeforeEach
    void init() {
        hashTable = new HashTable();
    }

    @Test
    void sizeEmpty() {
        assertEquals(0, hashTable.size());
    }

    @Test
    void size100DifferentElements() {
        String k = "k";
        String v = "v";
        for (int i = 0; i < 100; ++i) {
            hashTable.put(k, v);
            k += "k";
            v += "v";
        }
        assertEquals(100, hashTable.size());
    }

    @Test
    void size200RepeatingElements() {
        String k = "k";
        String v = "v";
        for (int i = 0; i < 100; ++i) {
            hashTable.put(k, v);
            k += "k";
            v += "v";
        }
        k = "k";
        v = "v";
        for (int i = 0; i < 100; ++i) {
            hashTable.put(k, v);
            k += "k";
            v += "v";
        }
        assertEquals(100, hashTable.size());
    }

    @Test
    void containsEmpty() {
        assertFalse(hashTable.contains("123"));
    }

    @Test
    void containsNullKey() {
        assertThrows(IllegalArgumentException.class,
                () -> { hashTable.contains(null); },
                "Key should not be null.");
    }

    @Test
    void containsExistingElement() {
        hashTable.put("my", "dog");
        assertTrue(hashTable.contains("my"));
    }

    @Test
    void containsNonExistingElement() {
        hashTable.put("my", "dog");
        assertFalse(hashTable.contains("your"));
    }

    @Test
    void getEmpty() {
        assertNull(hashTable.get("Hello"));
    }

    @Test
    void getNullKey() {
        assertThrows(IllegalArgumentException.class,
                () -> { hashTable.get(null); },
                "Key should not be null.");
    }

    @Test
    void getExistingElement() {
        hashTable.put("my", "dog");
        hashTable.put("your", "cat");
        assertEquals("cat", hashTable.get("your"));
        assertEquals("dog", hashTable.get("my"));
    }

    @Test
    void getNonExistingElement() {
        hashTable.put("my", "dog");
        hashTable.put("your", "cat");
        assertNull(hashTable.get("his"));
    }

    @Test
    void putNullKeyElement() {
        assertThrows(IllegalArgumentException.class,
                () -> { hashTable.put(null, "1"); },
                "Key should not be null.");
    }

    @Test
    void putNullValueElement() {
        assertThrows(IllegalArgumentException.class,
                () -> { hashTable.put("key", null); },
                "Value should not be null.");
    }

    @Test
    void putOneElement() {
        hashTable.put("my", "dog");
        assertEquals(1, hashTable.size());
    }

    @Test
    void putRepeatingElements() {
        for (int i = 0; i < 10; ++i) {
            hashTable.put("my", "dog");
        }
        assertEquals(1, hashTable.size());
        assertEquals("dog", hashTable.get("my"));
    }

    @Test
    void removeEmpty() {
        String value = hashTable.remove("1");
        assertEquals(0, hashTable.size());
        assertNull(hashTable.get("1"));
        assertNull(value);
    }

    @Test
    void removeNullElement() {
        assertThrows(IllegalArgumentException.class,
                () -> { hashTable.remove(null); },
                "Key should not be null.");
    }

    @Test
    void removeExistingElement() {
        hashTable.put("1", "one");
        hashTable.put("2", "two");
        hashTable.put("3", "three");
        String value = hashTable.remove("1");
        assertEquals(value, "one");
        assertEquals(2, hashTable.size());
        assertNull(hashTable.get("1"));
        assertEquals("two", hashTable.get("2"));
        assertEquals("three", hashTable.get("3"));
    }

    @Test
    void removeNonExistingElement() {
        hashTable.put("1", "one");
        hashTable.put("2", "two");
        hashTable.put("3", "three");
        String value = hashTable.remove("0");
        assertEquals(3, hashTable.size());
        assertNull(hashTable.get("0"));
        assertNull(value);
        assertEquals("one", hashTable.get("1"));
        assertEquals("two", hashTable.get("2"));
        assertEquals("three", hashTable.get("3"));
    }

    @Test
    void clearEmpty() {
        hashTable.clear();
        assertEquals(0, hashTable.size());
        assertNull(hashTable.get("smth"));
    }

    @Test
    void clear() {
        hashTable.put("1", "one");
        hashTable.put("2", "two");
        hashTable.put("3", "three");
        hashTable.put("4", "four");
        hashTable.put("5", "five");

        hashTable.clear();
        assertEquals(0, hashTable.size());
        assertNull(hashTable.get("1"));
        assertNull(hashTable.get("2"));
        assertNull(hashTable.get("3"));
        assertNull(hashTable.get("4"));
        assertNull(hashTable.get("5"));
    }
}