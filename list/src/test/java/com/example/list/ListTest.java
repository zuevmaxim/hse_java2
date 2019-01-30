package com.example.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ListTest {

    private List<Integer> list;

    @BeforeEach
    void init() {
        list = new List<>();
    }

    @Test
    void get() {
    }

    @Test
    void size() {
        list.add(1);
        list.add(2);
        assertEquals(2, list.size());
    }

    @Test
    void set() {
    }

    @Test
    void iterator() {
    }
}