package com.hse.java.md5;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Main {
    private static final int N = 10;
    private static final int FILE_SIZE = 1024;
    private static final int BUFFER_SIZE = 1024;
    private static final Random RANDOM = new Random(42);

    private static void fillRandomBytes(@NotNull File file) throws IOException {
        try (var out = new BufferedOutputStream(new FileOutputStream(file))) {
            var buffer = new byte[BUFFER_SIZE];
            for (int i = 0; i < FILE_SIZE; ++i) {
                RANDOM.nextBytes(buffer);
                out.write(buffer);
            }
        }
    }

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
