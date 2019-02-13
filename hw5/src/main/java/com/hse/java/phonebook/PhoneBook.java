package com.hse.java.phonebook;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

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

    public static class Pair<F, S> {
        private F first;
        private S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        public F getFirst() {
            return first;
        }

        public S getSecond() {
            return second;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Pair) {
                return first.equals(((Pair) obj).first) && second.equals(((Pair) obj).second);
            }
            return false;
        }
    }

    public ArrayList<Pair<String, String>> allPairs() throws SQLException {
        try(Connection connection = DriverManager.getConnection(dataBase)) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("select * from phones");
                var list = new ArrayList<Pair<String, String>>();
                while (resultSet.next()) {
                    list.add(new Pair<>(resultSet.getString("Holder"), resultSet.getString("Phone")));
                }
                return list;
            }
        }
    }
}
