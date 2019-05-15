package com.hse.java.md5;

import java.io.File;
import java.security.NoSuchAlgorithmException;

public class Main {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Enter directory path.");
                return;
            }
            var file = new File(args[0]);

            var timeOneThread = System.currentTimeMillis();
             MD5.hashOneThread(file);
            timeOneThread = System.currentTimeMillis() - timeOneThread;
            System.out.println("One thread time: " + timeOneThread);

            var timeForkJoin = System.currentTimeMillis();
            MD5.hashForkJoin(file);
            timeForkJoin = System.currentTimeMillis() - timeForkJoin;
            System.out.println("ForkJoin time: " + timeForkJoin);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("No algorithm MD5 found");
        }
    }
}
