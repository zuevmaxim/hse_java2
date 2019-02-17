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
    private final String DATABASE = "jdbc:sqlite:phones.db";

    /**
     * NoSuchRecordException erises if requested
     * a person name or phone which is no presented in the database.
     */
    public class NoSuchRecordException extends Exception {
        /**
         * Exception constructor.
         * @param error error description
         */
        NoSuchRecordException(String error) {
            super(error);
        }
    }

    /**
     * Phonebook constructor.
     * Creates a local database.
     * @throws SQLException if database error occurs
     */
    public PhoneBook() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate("drop table if exists persons");
                statement.executeUpdate(
                        "create table persons ("
                        + "id integer PRIMARY KEY AUTOINCREMENT ,"
                        + "name varchar NOT NULL ,"
                        + "unique (name))");
                statement.executeUpdate("drop table if exists phones");
                statement.executeUpdate(
                        "create table phones ("
                        + "id integer PRIMARY KEY AUTOINCREMENT ,"
                        + "phone varchar NOT NULL ,"
                        + "unique (phone))");
                statement.executeUpdate("drop table if exists phonebook");
                statement.executeUpdate(
                        "create table phonebook ("
                        + "personId integer NOT NULL ,"
                        + "phoneId integer NOT NULL ,"
                        + "unique (personId, phoneId) on conflict ignore,"
                        + "foreign key (personId) references persons(id),"
                        + "foreign key (phoneId) references phones(id))");
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
                        "select id from persons "
                        + "where name = '" + name + "'");
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
                        "select id from phones "
                        + "where phone = '" + phone + "'");
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
                        "insert or ignore into persons (name) values ('" + name + "')");
                statement.executeUpdate(
                        "insert or ignore into phones (phone) values ('" + phone + "')");
                statement.executeUpdate(
                        "insert into phonebook (personId, phoneId) "
                                + "values ('" + getPersonId(name) + "', '"
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
                        "select phones.phone from phones, phonebook "
                            + "where phones.id = phonebook.phoneId "
                            + "and phonebook.personId = '" + getPersonId(name) + "'");
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
                        "select persons.name from persons, phonebook "
                                + "where persons.id = phonebook.personId "
                                + "and phonebook.phoneId = '" + getPhoneId(phone) + "'");
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
                        "delete from phonebook where personId = '" + getPersonId(name)
                                + "' and phoneId = '" + getPhoneId(phone) + "'");
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
                        "insert or ignore into persons (name) values ('" + newName + "')");
                statement.executeUpdate(
                        "update phonebook set personId = '" + getPersonId(newName)
                                + "' where phoneId = '" + phoneId + "' "
                                + "and personId = '" + personId + "'");
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
                        "insert or ignore into phones (phone) values ('" + newPhone + "')");
                statement.executeUpdate(
                        "update phonebook set phoneId = '" + getPhoneId(newPhone)
                                + "' where phoneId = '" + phoneId + "' and personId = '" + personId + "'");
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
                        "select persons.name, phones.phone "
                                + "from persons, phones, phonebook "
                                + "where persons.id = phonebook.personId "
                                + "and phones.id = phonebook.phoneId");
                var list = new ArrayList<Pair<String, String>>();
                while (resultSet.next()) {
                    list.add(new Pair<>(resultSet.getString("name"),
                            resultSet.getString("phone")));
                }
                return list;
            }
        }
    }
}
