package com.hse.java.phonebook;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var in = new Scanner(System.in);

        PhoneBook phoneBook;
        try {
            phoneBook = new PhoneBook();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }

        while (true) {
            int command = in.nextInt();
            try {
                if (command == 0) {
                    return;
                } else if (command == 1) {
                    String name = in.next();
                    String phone = in.next();
                    phoneBook.add(name, phone);
                } else if (command == 2) {
                    String name = in.next();
                    var phoneList = phoneBook.findByName(name);
                    for (var phone : phoneList) {
                        System.out.print(phone);
                        System.out.print(' ');
                    }
                    System.out.println();
                } else if (command == 3) {
                    String phone = in.next();
                    var nameList = phoneBook.findByPhone(phone);
                    for (var name : nameList) {
                        System.out.print(name);
                        System.out.print(' ');
                    }
                    System.out.println();
                } else if (command == 4) {
                    String name = in.next();
                    String phone = in.next();
                    phoneBook.remove(name, phone);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.out.println(e.getCause().getMessage());
                return;
            }
        }
    }
}
