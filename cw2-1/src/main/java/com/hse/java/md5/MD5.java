package com.hse.java.md5;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private static final int SIZE = 4096;

    @NotNull
    public static byte[] hashOneThread(@NotNull File file) throws NoSuchAlgorithmException {
        var md = MessageDigest.getInstance("MD5");
        if (file.isFile()) {
            return hashFile(file);
        }
        md.digest(file.getName().getBytes());
        var children = file.listFiles();
        assert children != null; //file is a directory
        for (var child : children) {
            md.update(hashOneThread(child));
        }
        return md.digest();
    }

    @NotNull
    private static byte[] hashFile(@NotNull File file) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try (var in = new DigestInputStream(new FileInputStream(file), md)) {
            var buffer = new byte[SIZE];
            //noinspection StatementWithEmptyBody
            while (in.read(buffer) != -1);
            return md.digest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
