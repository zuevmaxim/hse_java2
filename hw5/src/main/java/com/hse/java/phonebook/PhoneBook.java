package com.hse.java.phonebook;

import java.sql.*;
import java.util.ArrayList;

public class PhoneBook {
    private final String dataBase;

    public PhoneBook() throws SQLException {
        dataBase = "jdbc:sqlite:sample.db";
        try(Connection connection = DriverManager.getConnection(dataBase)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate("drop table if exists phones");
                statement.executeUpdate("create table phones (Holder varchar , Phone varchar)");
            }
        }
    }

    public void add(String holder, String phone) throws SQLException {
        try(Connection connection = DriverManager.getConnection(dataBase)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate("insert into phones (Holder, Phone) values ('" + holder + "' , '" + phone + "')");
            }
        }
    }

    public ArrayList<String> findByName(String holder) throws SQLException {
        try(Connection connection = DriverManager.getConnection(dataBase)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("select Phone from phones where Holder = '" + holder + "'");
                var list = new ArrayList<String>();
                while (resultSet.next()) {
                    list.add(resultSet.getString("Phone"));
                }
                return list;
            }
        }
    }

    public ArrayList<String> findByPhone(String phone) throws SQLException {
        try(Connection connection = DriverManager.getConnection(dataBase)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(
                        "select Holder from phones where Phone = '" + phone + "'");
                var list = new ArrayList<String>();
                while (resultSet.next()) {
                    list.add(resultSet.getString("Holder"));
                }
                return list;
            }
        }
    }

    public void remove(String holder, String phone) throws SQLException {
        try(Connection connection = DriverManager.getConnection(dataBase)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate(
                        "delete from phones where Holder = '" + holder + "' and Phone = '" + phone + "'");
            }
        }
    }

    public void setName(String holder, String phone, String newName) throws SQLException {
        try(Connection connection = DriverManager.getConnection(dataBase)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate(
                        "update phones set Holder = '" + newName +
                                "' where Holder = '" + holder + "' and Phone = '" + phone + "'");
            }
        }
    }

    public void setPhone(String holder, String phone, String newPhone) throws SQLException {
        try(Connection connection = DriverManager.getConnection(dataBase)) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate(
                        "update phones set Phone = '" + newPhone +
                                "' where Holder = '" + holder + "' and Phone = '" + phone + "'");
            }
        }
    }
}
