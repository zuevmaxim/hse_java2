package com.example.hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ListTest {
    private List l;
    private Element[] elements = new Element[4];

    @BeforeEach
    void init() {
        l = new List();
        elements[0] = new Element("123", "bar");
        elements[1] = new Element("qwe", "foo");
        elements[2] = new Element("Ann", "9456890");
        elements[3] = new Element("a", "b");
    }

    @Test
    void findElementInEmptyList() {
        assertNull(l.find("123"));
    }

    @Test
    void findExistingElement() {
        l.insert(elements[0]);
        l.insert(elements[1]);
        assertEquals(l.find("123"), elements[0]);
    }

    @Test
    void findNonExistingElement() {
        l.insert(elements[0]);
        l.insert(elements[1]);
        assertNull(l.find("111"));
    }

    @Test
    void emptyListSize() {
        assertEquals(0, l.size());
    }

    @Test
    void insertOneElement() {
        l.insert(elements[3]);
        assertNotNull(l.find("a"));
        assertEquals(1, l.size());
    }

    @Test
    void insertElements() {
        l.insert(elements[0]);
        l.insert(elements[1]);
        l.insert(elements[2]);
        assertEquals(l.find("123"), elements[0]);
        assertEquals(l.find("qwe"), elements[1]);
        assertEquals(l.find("Ann"), elements[2]);
        assertEquals(3, l.size());
    }

    @Test
    void removeExistingElement() {
        l.insert(elements[0]);
        l.insert(elements[1]);
        l.insert(elements[2]);
        l.remove("qwe");
        assertNull(l.find("qwe"));
        assertEquals(2, l.size());
        assertEquals(l.find("123"), elements[0]);
        assertEquals(l.find("Ann"), elements[2]);
    }

    @Test
    void removeTailElement() {
        l.insert(elements[0]);
        l.insert(elements[1]);
        l.insert(elements[2]);
        l.remove("123");
        assertNull(l.find("123"));
        assertEquals(2, l.size());
        assertEquals(l.find("qwe"), elements[1]);
        assertEquals(l.find("Ann"), elements[2]);
    }

    @Test
    void removeHeadElement() {
        l.insert(elements[0]);
        l.insert(elements[1]);
        l.insert(elements[2]);
        l.remove("Ann");
        assertNull(l.find("Ann"));
        assertEquals(2, l.size());
        assertEquals(l.find("qwe"), elements[1]);
        assertEquals(l.find("123"), elements[0]);
    }

    @Test
    void removeNonExistingElement() {
        l.insert(elements[0]);
        l.insert(elements[1]);
        l.insert(elements[2]);
        l.remove("111");
        assertEquals(3, l.size());
        assertEquals(l.find("123"), elements[0]);
        assertEquals(l.find("qwe"), elements[1]);
        assertEquals(l.find("Ann"), elements[2]);
    }

    @Test
    void clear() {
        l.insert(elements[0]);
        l.insert(elements[1]);
        l.insert(elements[2]);
        l.clear();
        assertNull(l.find("123"));
        assertNull(l.find("qwe"));
        assertNull(l.find("Ann"));
        assertEquals(0, l.size());
    }

}