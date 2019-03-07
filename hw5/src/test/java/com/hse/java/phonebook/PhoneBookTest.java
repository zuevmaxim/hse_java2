package com.hse.java.phonebook;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PhoneBookTest {
    private PhoneBook phoneBook;

    @BeforeEach
    void setUp() throws SQLException {
        phoneBook = new PhoneBook("test.db");
    }

    @AfterEach
    void clean() throws SQLException {
        phoneBook.clean();
    }

    @Test
    void add() throws SQLException, PhoneBook.NoSuchRecordException {
        phoneBook.add("Jack", "1");
        assertEquals(Collections.singletonList("1"), phoneBook.findByName("Jack"));
    }

    @Test
    void addStrangeName() throws SQLException, PhoneBook.NoSuchRecordException {
        phoneBook.add("Robert'); DELETE FROM persons; --", "1");
        assertEquals(Collections.singletonList("1"), phoneBook.findByName("Robert'); DELETE FROM persons; --"));
    }

    @Test
    void findByName() throws SQLException, PhoneBook.NoSuchRecordException {
        phoneBook.add("Jack", "1");
        phoneBook.add("John", "1");
        phoneBook.add("Mike", "2");
        phoneBook.add("John", "3");
        assertEquals(Collections.singletonList("1"), phoneBook.findByName("Jack"));
        assertEquals(Arrays.asList("1", "3"), phoneBook.findByName("John"));
        assertEquals(Collections.singletonList("2"), phoneBook.findByName("Mike"));
    }

    @Test
    void findByNameThrows() {
        assertThrows(PhoneBook.NoSuchRecordException.class,
                () -> phoneBook.findByName("Jack"));
    }

    @Test
    void findByPhone() throws SQLException, PhoneBook.NoSuchRecordException {
        phoneBook.add("Jack", "1");
        phoneBook.add("John", "1");
        phoneBook.add("Mike", "2");
        phoneBook.add("John", "3");
        assertEquals(Arrays.asList("Jack", "John"), phoneBook.findByPhone("1"));
        assertEquals(Collections.singletonList("Mike"), phoneBook.findByPhone("2"));
        assertEquals(Collections.singletonList("John"), phoneBook.findByPhone("3"));
    }

    @Test
    void findByPhoneThrows() {
        assertThrows(PhoneBook.NoSuchRecordException.class,
                () -> phoneBook.findByPhone("123"));
    }

    @Test
    void remove() throws SQLException, PhoneBook.NoSuchRecordException {
        phoneBook.add("Jack", "1");
        phoneBook.add("John", "1");
        phoneBook.add("Mike", "2");

        phoneBook.remove("John", "1");

        assertEquals(Collections.singletonList("1"), phoneBook.findByName("Jack"));
        assertEquals(Collections.EMPTY_LIST, phoneBook.findByName("John"));
        assertEquals(Collections.singletonList("2"), phoneBook.findByName("Mike"));

        assertEquals(Collections.singletonList("Jack"), phoneBook.findByPhone("1"));
        assertEquals(Collections.singletonList("Mike"), phoneBook.findByPhone("2"));
    }

    @Test
    void removeThrows() {
        assertThrows(PhoneBook.NoSuchRecordException.class,
                () -> phoneBook.remove("Max", "1"));
    }

    @Test
    void setName() throws SQLException, PhoneBook.NoSuchRecordException {
        phoneBook.add("Jack", "1");
        phoneBook.add("John", "1");
        phoneBook.add("Mike", "2");
        phoneBook.add("John", "3");
        phoneBook.setName("Jack", "1", "Tom");
        assertEquals(Collections.singletonList("1"), phoneBook.findByName("Tom"));
        assertEquals(Collections.EMPTY_LIST, phoneBook.findByName("Jack"));
    }

    @Test
    void setNameThrows() throws SQLException {
        phoneBook.add("Jack", "1");
        phoneBook.add("John", "1");
        phoneBook.add("Mike", "2");
        assertThrows(PhoneBook.NoSuchRecordException.class,
                () -> phoneBook.setName("Jack", "12", "Tom"));
    }

    @Test
    void setPhone() throws SQLException, PhoneBook.NoSuchRecordException {
        phoneBook.add("Jack", "1");
        phoneBook.add("John", "1");
        phoneBook.add("Mike", "2");
        phoneBook.add("John", "3");
        phoneBook.setPhone("Jack", "1", "4");
        assertEquals(Collections.singletonList("4"), phoneBook.findByName("Jack"));
        assertEquals(Collections.singletonList("John"), phoneBook.findByPhone("1"));
    }

    @Test
    void setPhoneThrows() throws SQLException {
        phoneBook.add("Jack", "1");
        phoneBook.add("John", "1");
        phoneBook.add("Mike", "2");
        assertThrows(PhoneBook.NoSuchRecordException.class,
                () -> phoneBook.setPhone("Jack1", "12", "Tom"));
    }

    @Test
    void allPairs() throws SQLException {
        phoneBook.add("Jack", "1");
        phoneBook.add("John", "1");
        phoneBook.add("Mike", "2");
        phoneBook.add("John", "3");
        phoneBook.add("John", "3");
        assertEquals(Arrays.asList(
                new Pair<>("Jack", "1"),
                new Pair<>("John", "1"),
                new Pair<>("Mike", "2"),
                new Pair<>("John", "3")), phoneBook.allPairs());
    }
}