package com.hse.java.phonebook;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Realize a phonebook.
 * Contains the information about people and their phone numbers.
 */
public class PhoneBook {
    /** Database is located in the project's root. */
    private final String dataBase;

    /**
     * NoSuchRecordException erises if requested
     * a person name or phone which is no presented in the database.
     */
    public static class NoSuchRecordException extends Exception {
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
     * @param databasePath path to the local database
     * @throws SQLException if database error occurs
     */
    public PhoneBook(@NotNull String databasePath) throws SQLException {
        dataBase = "jdbc:sqlite:" + databasePath;
        try (Connection connection = DriverManager.getConnection(dataBase)) {
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
        try (Connection connection = DriverManager.getConnection(dataBase)) {
            var statement = "SELECT id FROM persons WHERE name = ?";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, name);
                ResultSet resultSet = preparedStatement.executeQuery();
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
        try (Connection connection = DriverManager.getConnection(dataBase)) {
            var statement = "SELECT id FROM phones WHERE phone = ?";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, phone);
                ResultSet resultSet = preparedStatement.executeQuery();
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
        try (Connection connection = DriverManager.getConnection(dataBase)) {
            var insertIntoPersons = "INSERT OR IGNORE INTO persons (name) VALUES (?)";
            var insertIntoPhones = "INSERT OR IGNORE INTO  phones (phone)  VALUES (?)";
            var insertIntoPhonebook = "INSERT INTO phonebook (personId, phoneId) VALUES (?, ?)";
            try (var personsPreparedStatement = connection.prepareStatement(insertIntoPersons);
                    var phonesPreparedStatement = connection.prepareStatement(insertIntoPhones);
                    var phonebookPreparedStatement = connection.prepareStatement(insertIntoPhonebook)) {
                personsPreparedStatement.setString(1, name);
                personsPreparedStatement.executeUpdate();
                phonesPreparedStatement.setString(1, phone);
                phonesPreparedStatement.executeUpdate();
                phonebookPreparedStatement.setString(1, getPersonId(name));
                phonebookPreparedStatement.setString(2, getPhoneId(phone));
                phonebookPreparedStatement.executeUpdate();
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
    public List<String> findByName(@NotNull String name)
            throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(dataBase)) {
            var statement = "SELECT phones.phone FROM phones, phonebook WHERE phones.id = phonebook.phoneId "
                    + "AND phonebook.personId = ?";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, getPersonId(name));
                ResultSet resultSet = preparedStatement.executeQuery();
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
    public List<String> findByPhone(@NotNull String phone)
            throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(dataBase)) {
            var statement = "SELECT persons.name FROM persons, phonebook WHERE persons.id = phonebook.personId "
                    + "AND phonebook.phoneId = ?";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, getPhoneId(phone));
                ResultSet resultSet = preparedStatement.executeQuery();
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
        try (Connection connection = DriverManager.getConnection(dataBase)) {
            var statement = "DELETE FROM phonebook WHERE personId = ? AND phoneId = ?";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, getPersonId(name));
                preparedStatement.setString(2, getPhoneId(phone));
                preparedStatement.executeUpdate();
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
        try (Connection connection = DriverManager.getConnection(dataBase)) {
            var insertNewName = "INSERT OR IGNORE INTO persons (name) VALUES (?)";
            var setNewName = "UPDATE phonebook SET personId = ? WHERE phoneId = ? AND personId = ?";
            //noinspection Duplicates
            try (var insertPreparedStatement = connection.prepareStatement(insertNewName);
                    var setPreparedStatement = connection.prepareStatement(setNewName)) {
                var personId = getPersonId(name);
                var phoneId = getPhoneId(phone);
                insertPreparedStatement.setString(1, newName);
                insertPreparedStatement.executeUpdate();
                setPreparedStatement.setString(1, getPersonId(newName));
                setPreparedStatement.setString(2, phoneId);
                setPreparedStatement.setString(3, personId);
                setPreparedStatement.executeUpdate();
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
        try (Connection connection = DriverManager.getConnection(dataBase)) {
            var insertNewPhone = "INSERT OR IGNORE INTO phones (phone) VALUES (?)";
            var setNewPhone = "UPDATE phonebook SET phoneId = ? WHERE phoneId = ? AND personId = ?";
            //noinspection Duplicates
            try (var insertPreparedStatement = connection.prepareStatement(insertNewPhone);
                 var setPreparedStatement = connection.prepareStatement(setNewPhone)) {
                var personId = getPersonId(name);
                var phoneId = getPhoneId(phone);
                insertPreparedStatement.setString(1, newPhone);
                insertPreparedStatement.executeUpdate();
                setPreparedStatement.setString(1, getPhoneId(newPhone));
                setPreparedStatement.setString(2, phoneId);
                setPreparedStatement.setString(3, personId);
                setPreparedStatement.executeUpdate();
            }
        }
    }

    /**
     * Find all pairs (person, phone).
     * @return list of pairs (person, phone)
     * @throws SQLException if database error occurs
     */
    @NotNull
    public List<Pair<String, String>> allPairs() throws SQLException {
        try (Connection connection = DriverManager.getConnection(dataBase)) {
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
    public void clean() throws SQLException {
        try (Connection connection = DriverManager.getConnection(dataBase)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM phonebook");
                statement.executeUpdate("DELETE FROM persons");
                statement.executeUpdate("DELETE FROM phones");
            }
        }
    }
}
