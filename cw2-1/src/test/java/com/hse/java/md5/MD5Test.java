package com.hse.java.md5;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MD5Test {
    private static final int FILE_SIZE = 1024;
    private static final int BUFFER_SIZE = 1024;
    private static final Random RANDOM = new Random(7);

    private static void fillRandomBytes(@NotNull File file) throws IOException {
        try (var out = new BufferedOutputStream(new FileOutputStream(file))) {
            var buffer = new byte[BUFFER_SIZE];
            for (int i = 0; i < FILE_SIZE; ++i) {
                RANDOM.nextBytes(buffer);
                out.write(buffer);
            }
        }
    }

    private static void fillDirectory(@NotNull File file, int depth) throws IOException {
        if (depth == 0) {
            return;
        }
        int subDirsNumber = RANDOM.nextInt(4);
        for (int i = 0; i < subDirsNumber; ++i) {
            var subDir = Files.createTempDirectory(file.toPath(), "sub_dir").toFile();
            fillDirectory(subDir, depth - 1);
        }
        int filesNumber = RANDOM.nextInt(3);
        for (int i = 0; i < filesNumber; ++i) {
            var subFile = Files.createTempFile(file.toPath(), "sub_file", ".tmp").toFile();
            fillRandomBytes(subFile);
        }
    }


    private static List<File> directories = new ArrayList<>();

    @BeforeAll
    static void initTempFiles() throws IOException {
        directories.add(Files.createTempDirectory("empty_dir_test").toFile());
        directories.add(Files.createTempFile("test_file", ".tmp").toFile());
        var directory = Files.createTempDirectory("test_dir").toFile();
        fillDirectory(directory, 5);
        directories.add(directory);
    }

    @Test
    void testOneThreadAndForkJoinResultsAreEqual() throws NoSuchAlgorithmException {
        for (var directory : directories) {
            var hash1 = MD5.hashOneThread(directory);
            var hash2 = MD5.hashForkJoin(directory);
            assertArrayEquals(hash1, hash2);
        }
    }

}