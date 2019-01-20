package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ListTest {

    @Test
    void findElementInEmptyList() {
        List l = new List();
        assertNull(l.find("123"));
    }

    @Test
    void findExistingElement() {
        List l = new List();
        Elem e1 = new Elem("123", "bar");
        Elem e2 = new Elem("qwe", "foo");
        l.insert(e1);
        l.insert(e2);
        assertTrue(l.find("123").eq(e1));
    }

    @Test
    void findNonExistingElement() {
        List l = new List();
        Elem e1 = new Elem("123", "bar");
        Elem e2 = new Elem("qwe", "foo");
        l.insert(e1);
        l.insert(e2);
        assertNull(l.find("111"));
    }

    @Test
    void emptyListSize() {
        List l = new List();
        assertEquals(0, l.size());
    }

    @Test
    void insertOneElement() {
        List l = new List();
        Elem e = new Elem("a", "b");
        l.insert(e);
        assertFalse(l.find("a") == null);
        assertEquals(1, l.size());
    }

    @Test
    void insertElements() {
        List l = new List();
        Elem e1 = new Elem("123", "bar");
        Elem e2 = new Elem("qwe", "foo");
        Elem e3 = new Elem("Ann", "9456890");
        l.insert(e1);
        l.insert(e2);
        l.insert(e3);
        assertTrue(l.find("123").eq(e1));
        assertTrue(l.find("qwe").eq(e2));
        assertTrue(l.find("Ann").eq(e3));
        assertEquals(3, l.size());
    }

    @Test
    void removeExistingElement() {
        List l = new List();
        Elem e1 = new Elem("123", "bar");
        Elem e2 = new Elem("qwe", "foo");
        Elem e3 = new Elem("Ann", "9456890");
        l.insert(e1);
        l.insert(e2);
        l.insert(e3);
        l.remove("qwe");
        assertNull(l.find("qwe"));
        assertEquals(2, l.size());
    }

    @Test
    void removeTailElement() {
        List l = new List();
        Elem e1 = new Elem("123", "bar");
        Elem e2 = new Elem("qwe", "foo");
        Elem e3 = new Elem("Ann", "9456890");
        l.insert(e1);
        l.insert(e2);
        l.insert(e3);
        l.remove("123");
        assertNull(l.find("123"));
        assertEquals(2, l.size());
    }

    @Test
    void removeHeadElement() {
        List l = new List();
        Elem e1 = new Elem("123", "bar");
        Elem e2 = new Elem("qwe", "foo");
        Elem e3 = new Elem("Ann", "9456890");
        l.insert(e1);
        l.insert(e2);
        l.insert(e3);
        l.remove("Ann");
        assertNull(l.find("Ann"));
        assertEquals(2, l.size());
    }

    @Test
    void removeNonExistingElement() {
        List l = new List();
        Elem e1 = new Elem("123", "bar");
        Elem e2 = new Elem("qwe", "foo");
        l.insert(e1);
        l.insert(e2);
        l.remove("111");
        assertTrue(l.find("qwe").eq(e2));
        assertEquals(2, l.size());
    }

    @Test
    void clear() {
        List l = new List();
        Elem e1 = new Elem("123", "bar");
        Elem e2 = new Elem("qwe", "foo");
        Elem e3 = new Elem("Ann", "9456890");
        l.insert(e1);
        l.insert(e2);
        l.insert(e3);
        l.clear();
        assertNull(l.find("123"));
        assertNull(l.find("qwe"));
        assertNull(l.find("Ann"));
        assertEquals(0, l.size());
    }

}