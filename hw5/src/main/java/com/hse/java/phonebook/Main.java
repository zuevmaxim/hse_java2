package com.hse.java.phonebook;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * Class for user interface.
 */
public class Main {
    /**
     * Main function for command line interface.
     */
    public static void main(String[] args) {
        var in = new Scanner(System.in);
        try {
            var phoneBook = new PhoneBook("phonebook.db");
            while (true) {
                System.out.print("Enter command: ");
                String command = in.next();
                try {
                    switch (command) {
                        case "0": {
                            return;
                        }
                        case "1": {
                            System.out.print("Enter name: ");
                            String name = in.next();
                            System.out.print("Enter phone: ");
                            String phone = in.next();
                            phoneBook.add(name, phone);
                            break;
                        }
                        case "2": {
                            System.out.print("Enter name: ");
                            String name = in.next();
                            var phoneList = phoneBook.findByName(name);
                            System.out.println(phoneList);
                            break;
                        }
                        case "3": {
                            System.out.print("Enter phone: ");
                            String phone = in.next();
                            var nameList = phoneBook.findByPhone(phone);
                            System.out.println(nameList);
                            break;
                        }
                        case "4": {
                            System.out.print("Enter name: ");
                            String name = in.next();
                            System.out.print("Enter phone: ");
                            String phone = in.next();
                            phoneBook.remove(name, phone);
                            break;
                        }
                        case "5": {
                            System.out.print("Enter name: ");
                            String name = in.next();
                            System.out.print("Enter phone: ");
                            String phone = in.next();
                            System.out.print("Enter new owner: ");
                            String newName = in.next();
                            phoneBook.setName(name, phone, newName);
                            break;
                        }
                        case "6": {
                            System.out.print("Enter name: ");
                            String name = in.next();
                            System.out.print("Enter phone: ");
                            String phone = in.next();
                            System.out.print("Enter new phone: ");
                            String newPhone = in.next();
                            phoneBook.setPhone(name, phone, newPhone);
                            break;
                        }
                        case "7": {
                            var pairs = phoneBook.allPairs();
                            for (var pair : pairs) {
                                System.out.print(pair.getFirst());
                                System.out.print(' ');
                                System.out.println(pair.getSecond());
                            }
                            break;
                        }
                        default: {
                            System.out.println("Enter command number from 0 to 7.");
                        }
                    }
                } catch (PhoneBook.NoSuchRecordException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}
