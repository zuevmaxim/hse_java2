package com.hse.java.md5;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
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
            var directory = Files.createTempDirectory("temp").toFile();
            for (int i = 0; i < N; ++i) {
                var file = Files.createTempFile(directory.toPath(), "tmp", ".tmp").toFile();
                fillRandomBytes(file);
            }

            var hash = MD5.hashOneThread(directory);
            for (var b : hash) {
                System.out.print(b);
                System.out.print(" ");
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }
}
