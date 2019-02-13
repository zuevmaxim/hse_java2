package com.hse.java.phonebook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PhoneBookTest {
    private PhoneBook phoneBook;

    @BeforeEach
    void setUp() throws SQLException {
        phoneBook = new PhoneBook();
    }

    @Test
    void add() throws SQLException {
        phoneBook.add("Jack", "1");
        assertEquals(new ArrayList<>(Collections.singletonList("1")), phoneBook.findByName("Jack"));
    }

    @Test
    void findByName() throws SQLException {
        phoneBook.add("Jack", "1");
        phoneBook.add("John", "1");
        phoneBook.add("Mike", "2");
        phoneBook.add("John", "3");
        assertEquals(new ArrayList<>(Collections.singletonList("1")), phoneBook.findByName("Jack"));
        assertEquals(new ArrayList<>(Arrays.asList("1", "3")), phoneBook.findByName("John"));
        assertEquals(new ArrayList<>(Collections.singletonList("2")), phoneBook.findByName("Mike"));
    }

    @Test
    void findByPhone() throws SQLException {
        phoneBook.add("Jack", "1");
        phoneBook.add("John", "1");
        phoneBook.add("Mike", "2");
        phoneBook.add("John", "3");
        assertEquals(new ArrayList<>(Arrays.asList("Jack", "John")), phoneBook.findByPhone("1"));
        assertEquals(new ArrayList<>(Collections.singletonList("Mike")), phoneBook.findByPhone("2"));
        assertEquals(new ArrayList<>(Collections.singletonList("John")), phoneBook.findByPhone("3"));

    }

    @Test
    void remove() throws SQLException {
        phoneBook.add("Jack", "1");
        phoneBook.add("John", "1");
        phoneBook.add("Mike", "2");

        phoneBook.remove("John", "1");

        assertEquals(new ArrayList<>(Collections.singletonList("1")), phoneBook.findByName("Jack"));
        assertEquals(new ArrayList<String>(), phoneBook.findByName("John"));
        assertEquals(new ArrayList<>(Collections.singletonList("2")), phoneBook.findByName("Mike"));

        assertEquals(new ArrayList<>(Collections.singletonList("Jack")), phoneBook.findByPhone("1"));
        assertEquals(new ArrayList<>(Collections.singletonList("Mike")), phoneBook.findByPhone("2"));
    }
}