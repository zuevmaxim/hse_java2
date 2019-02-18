package com.hse.java.phonebook;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;

/**
 * Realize a phonebook.
 * Contains the information about people and their phone numbers.
 */
public class PhoneBook {
    /**
     * Database is located in the project's root.
     */
    private final String DATABASE;

    /**
     * NoSuchRecordException erises if requested
     * a person name or phone which is no presented in the database.
     */
    public class NoSuchRecordException extends Exception {
        /**
         * Exception constructor.
         * @param error error description
         */
        NoSuchRecordException(@NotNull String error) {
            super(error);
        }
    }

    /**
     * Phonebook constructor.
     * Creates a local database.
     * @throws SQLException if database error occurs
     */
    public PhoneBook(@NotNull String databasePath) throws SQLException {
        DATABASE = "jdbc:sqlite:" + databasePath;
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS persons ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT ,"
                        + "name VARCHAR NOT NULL ,"
                        + "UNIQUE (name))");
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS phones ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT ,"
                        + "phone VARCHAR NOT NULL ,"
                        + "UNIQUE (phone))");
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS phonebook ("
                        + "personId INTEGER NOT NULL ,"
                        + "phoneId INTEGER NOT NULL ,"
                        + "UNIQUE (personId, phoneId) ON CONFLICT IGNORE ,"
                        + "FOREIGN KEY (personId) REFERENCES persons(id),"
                        + "FOREIGN KEY (phoneId) REFERENCES phones(id))");
            }
        }
    }

    /**
     * Find person's id by name.
     * @return person's id
     * @throws SQLException if database error occurs
     * @throws NoSuchRecordException if there is no person with such name
     */
    @NotNull
    private String getPersonId(@NotNull String name)
            throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(
                        "SELECT id FROM persons "
                        + "WHERE name = '" + name + "'");
                if (!resultSet.next()) {
                    throw new NoSuchRecordException(
                            "There is no person with such name: " + name);
                }
                return resultSet.getString("id");
            }
        }
    }

    /**
     * Find phone's id by phone.
     * @return phone's id
     * @throws SQLException if database error occurs
     * @throws NoSuchRecordException if there is no such phone number
     */
    @NotNull
    private String getPhoneId(@NotNull String phone)
            throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(
                        "SELECT id FROM phones "
                        + "WHERE phone = '" + phone + "'");
                if (!resultSet.next()) {
                    throw new NoSuchRecordException("There is no such phone: " + phone);
                }
                return resultSet.getString("id");
            }
        }
    }

    /**
     * Add pair (person, phone) to the database.
     * @param name person's name
     * @param phone person's phone
     * @throws SQLException if database error occurs
     */
    public void add(@NotNull String name, @NotNull String phone)
            throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate(
                        "INSERT OR IGNORE INTO persons (name) "
                                + "VALUES ('" + name + "')");
                statement.executeUpdate(
                        "INSERT OR IGNORE INTO phones (phone) "
                                + "VALUES ('" + phone + "')");
                statement.executeUpdate(
                        "INSERT INTO phonebook (personId, phoneId) "
                                + "VALUES ('" + getPersonId(name) + "', '"
                                + getPhoneId(phone) + "')");
            } catch (NoSuchRecordException e) {
                assert false;
            }
        }
    }

    /**
     * Find all phones held by this person.
     * @param name person's name
     * @return list of person's phones
     * @throws SQLException if database error occurs
     * @throws NoSuchRecordException is there is no person with such name
     */
    @NotNull
    public ArrayList<String> findByName(@NotNull String name)
            throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(
                        "SELECT phones.phone FROM phones, phonebook "
                            + "WHERE phones.id = phonebook.phoneId "
                            + "AND phonebook.personId = '" + getPersonId(name) + "'");
                var list = new ArrayList<String>();
                while (resultSet.next()) {
                    list.add(resultSet.getString("phone"));
                }
                return list;
            }
        }
    }

    /**
     * Find all holder of this person.
     * @param phone person's phone
     * @return list of persons
     * @throws SQLException if database error occurs
     * @throws NoSuchRecordException if there is no such phone number
     */
    @NotNull
    public ArrayList<String> findByPhone(@NotNull String phone)
            throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(
                        "SELECT persons.name FROM persons, phonebook "
                                + "WHERE persons.id = phonebook.personId "
                                + "AND phonebook.phoneId = '" + getPhoneId(phone) + "'");
                var list = new ArrayList<String>();
                while (resultSet.next()) {
                    list.add(resultSet.getString("name"));
                }
                return list;
            }
        }
    }

    /**
     * Remove pair (person, phone) from the database.
     * @param name person's name
     * @param phone person's phone
     * @throws SQLException if database error occurs
     * @throws NoSuchRecordException if there is no such person or phone
     */
    public void remove(@NotNull String name, @NotNull String phone)
            throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate(
                        "DELETE FROM phonebook WHERE personId = '" + getPersonId(name)
                                + "' AND phoneId = '" + getPhoneId(phone) + "'");
            }
        }
    }

    /**
     * Set new phone holder.
     * @param name previous phone holder
     * @param phone phone number
     * @param newName new phone holder
     * @throws SQLException if database error occurs
     * @throws NoSuchRecordException if there is no such person or phone
     */
    public void setName(@NotNull String name,
                        @NotNull String phone, @NotNull String newName)
            throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                var personId = getPersonId(name);
                var phoneId = getPhoneId(phone);
                statement.executeUpdate(
                        "INSERT OR IGNORE INTO persons (name) "
                                + "VALUES ('" + newName + "')");
                statement.executeUpdate(
                        "UPDATE phonebook SET personId = '" + getPersonId(newName)
                                + "' WHERE phoneId = '" + phoneId + "' "
                                + "AND personId = '" + personId + "'");
            }
        }
    }

    /**
     * Set new phone for the person.
     * @param name phone holder
     * @param phone previous phone number
     * @param newPhone new phone number
     * @throws SQLException if database error occurs
     * @throws NoSuchRecordException if there is no such person or phone
     */
    public void setPhone(@NotNull String name,
                         @NotNull String phone, @NotNull String newPhone)
            throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                var personId = getPersonId(name);
                var phoneId = getPhoneId(phone);
                statement.executeUpdate(
                        "INSERT OR IGNORE INTO phones (phone) "
                        + "VALUES ('" + newPhone + "')");
                statement.executeUpdate(
                        "UPDATE phonebook SET phoneId = '" + getPhoneId(newPhone)
                                + "' WHERE phoneId = '" + phoneId + "' AND personId = '" + personId + "'");
            }
        }
    }

    /**
     * Find all pairs (person, phone).
     * @return list of pairs (person, phone)
     * @throws SQLException if database error occurs
     */
    @NotNull
    public ArrayList<Pair<String, String>> allPairs() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(
                        "SELECT persons.name, phones.phone "
                                + "FROM persons, phones, phonebook "
                                + "WHERE persons.id = phonebook.personId "
                                + "AND phones.id = phonebook.phoneId");
                var list = new ArrayList<Pair<String, String>>();
                while (resultSet.next()) {
                    list.add(new Pair<>(resultSet.getString("name"),
                            resultSet.getString("phone")));
                }
                return list;
            }
        }
    }

    /**
     * Clear database.
     * @throws SQLException if database error occurs
     */
    void clean() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM phonebook");
                statement.executeUpdate("DELETE FROM persons");
                statement.executeUpdate("DELETE FROM phones");
            }
        }
    }
}
