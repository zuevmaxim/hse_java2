package com.example.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ListTest {
    private List list;
    private Element[] elements = new Element[4];

    @BeforeEach
    void init() {
        list = new List();
        elements[0] = new Element("123", "bar");
        elements[1] = new Element("qwe", "foo");
        elements[2] = new Element("Ann", "9456890");
        elements[3] = new Element("a", "b");
    }

    @Test
    void findElementInEmptyList() {
        assertNull(list.find("123"));
    }

    @Test
    void findExistingElement() {
        list.insert(elements[0]);
        list.insert(elements[1]);
        assertEquals(list.find("123"), elements[0]);
    }

    @Test
    void findNonExistingElement() {
        list.insert(elements[0]);
        list.insert(elements[1]);
        assertNull(list.find("111"));
    }

    @Test
    void emptyListSize() {
        assertEquals(0, list.size());
    }

    @Test
    void insertOneElement() {
        list.insert(elements[3]);
        assertNotNull(list.find("a"));
        assertEquals(1, list.size());
    }

    @Test
    void insertElements() {
        list.insert(elements[0]);
        list.insert(elements[1]);
        list.insert(elements[2]);
        assertEquals(list.find("123"), elements[0]);
        assertEquals(list.find("qwe"), elements[1]);
        assertEquals(list.find("Ann"), elements[2]);
        assertEquals(3, list.size());
    }

    @Test
    void removeExistingElement() {
        list.insert(elements[0]);
        list.insert(elements[1]);
        list.insert(elements[2]);
        list.remove("qwe");
        assertNull(list.find("qwe"));
        assertEquals(2, list.size());
        assertEquals(list.find("123"), elements[0]);
        assertEquals(list.find("Ann"), elements[2]);
    }

    @Test
    void removeTailElement() {
        list.insert(elements[0]);
        list.insert(elements[1]);
        list.insert(elements[2]);
        list.remove("123");
        assertNull(list.find("123"));
        assertEquals(2, list.size());
        assertEquals(list.find("qwe"), elements[1]);
        assertEquals(list.find("Ann"), elements[2]);
    }

    @Test
    void removeHeadElement() {
        list.insert(elements[0]);
        list.insert(elements[1]);
        list.insert(elements[2]);
        list.remove("Ann");
        assertNull(list.find("Ann"));
        assertEquals(2, list.size());
        assertEquals(list.find("qwe"), elements[1]);
        assertEquals(list.find("123"), elements[0]);
    }

    @Test
    void removeNonExistingElement() {
        list.insert(elements[0]);
        list.insert(elements[1]);
        list.insert(elements[2]);
        list.remove("111");
        assertEquals(3, list.size());
        assertEquals(list.find("123"), elements[0]);
        assertEquals(list.find("qwe"), elements[1]);
        assertEquals(list.find("Ann"), elements[2]);
    }

    @Test
    void clear() {
        list.insert(elements[0]);
        list.insert(elements[1]);
        list.insert(elements[2]);
        list.clear();
        assertNull(list.find("123"));
        assertNull(list.find("qwe"));
        assertNull(list.find("Ann"));
        assertEquals(0, list.size());
    }

}