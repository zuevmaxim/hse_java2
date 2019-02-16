package com.hse.java.phonebook;

import java.sql.*;
import java.util.ArrayList;

public class PhoneBook {
    private final String DATABASE;

    public class NoSuchRecordException extends Exception {
        NoSuchRecordException(String error) {
            super(error);
        }
    }

    public PhoneBook() throws SQLException {
        DATABASE = "jdbc:sqlite:phones.db";
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate("drop table if exists persons");
                statement.executeUpdate("create table persons (" +
                        "id integer PRIMARY KEY AUTOINCREMENT ," +
                        "name varchar NOT NULL ," +
                        "unique (name))");
                statement.executeUpdate("drop table if exists phones");
                statement.executeUpdate("create table phones (" +
                        "id integer PRIMARY KEY AUTOINCREMENT ," +
                        "phone varchar NOT NULL ," +
                        "unique (phone))");
                statement.executeUpdate("drop table if exists phonebook");
                statement.executeUpdate("create table phonebook (" +
                        "personId integer NOT NULL ," +
                        "phoneId integer NOT NULL ," +
                        "unique (personId, phoneId) on conflict ignore," +
                        "foreign key (personId) references persons(id)," +
                        "foreign key (phoneId) references phones(id))");
            }
        }
    }

    private String getPersonId(String name) throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("select id from persons " +
                        "where name = '" + name + "'" );
                if (!resultSet.next()) {
                    throw new NoSuchRecordException("There is no person with such name: " + name);
                }
                return resultSet.getString("id");
            }
        }
    }

    private String getPhoneId(String phone) throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("select id from phones " +
                        "where phone = '" + phone + "'" );
                if (!resultSet.next()) {
                    throw new NoSuchRecordException("There is no such phone: " + phone);
                }
                return resultSet.getString("id");
            }
        }
    }

    public void add(String name, String phone) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                //statement.executeUpdate("insert into phones (Holder, Phone) values ('" + holder + "' , '" + phone + "')");
                statement.executeUpdate("insert or ignore into persons (name) values ('" + name + "')");
                statement.executeUpdate("insert or ignore into phones (phone) values ('" + phone + "')");
                statement.executeUpdate("insert into phonebook (personId, phoneId) values" +
                        "('" + getPersonId(name) + "', '" + getPhoneId(phone) + "')");
            } catch (NoSuchRecordException e) {
                assert false;
            }
        }
    }

    public ArrayList<String> findByName(String name) throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(
                        "select phones.phone from phones, phonebook " +
                            "where phones.id = phonebook.phoneId " +
                            "and phonebook.personId = '" + getPersonId(name) + "'");
                var list = new ArrayList<String>();
                while (resultSet.next()) {
                    list.add(resultSet.getString("phone"));
                }
                return list;
            }
        }
    }

    public ArrayList<String> findByPhone(String phone) throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(
                        "select persons.name from persons, phonebook " +
                                "where persons.id = phonebook.personId " +
                                "and phonebook.phoneId = '" + getPhoneId(phone) + "'");
                var list = new ArrayList<String>();
                while (resultSet.next()) {
                    list.add(resultSet.getString("name"));
                }
                return list;
            }
        }
    }

    public void remove(String name, String phone) throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate(
                        "delete from phonebook where personId = '" + getPersonId(name) +
                                "' and phoneId = '" + getPhoneId(phone) + "'");
            }
        }
    }

    public void setName(String name, String phone, String newName) throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                var personId = getPersonId(name);
                var phoneId = getPhoneId(phone);
                statement.executeUpdate("insert or ignore into persons (name) values ('" + newName + "')");
                statement.executeUpdate(
                        "update phonebook set personId = '" + getPersonId(newName)
                                + "' where phoneId = '" + phoneId + "' and personId = '" + personId + "'");
            }
        }
    }

    public void setPhone(String name, String phone, String newPhone) throws SQLException, NoSuchRecordException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                var personId = getPersonId(name);
                var phoneId = getPhoneId(phone);
                statement.executeUpdate("insert or ignore into phones (phone) values ('" + newPhone + "')");
                statement.executeUpdate(
                        "update phonebook set phoneId = '" + getPhoneId(newPhone)
                                + "' where phoneId = '" + phoneId+ "' and personId = '" + personId + "'");
            }
        }
    }

    public ArrayList<Pair<String, String>> allPairs() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(
                        "select persons.name, phones.phone " +
                                "from persons, phones, phonebook " +
                                "where persons.id = phonebook.personId " +
                                "and phones.id = phonebook.phoneId");
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
