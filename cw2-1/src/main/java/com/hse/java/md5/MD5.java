package com.hse.java.md5;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

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
        if (children != null) {
            for (var child : children) {
                md.update(hashOneThread(child));
            }
        }
        return md.digest();
    }

    @NotNull
    public static byte[] hashForkJoin(@NotNull File file) {
        return ForkJoinPool.commonPool().invoke(new HashTask(file));
    }

    private static class HashTask extends RecursiveTask<byte[]> {
        private File file;

        private HashTask(@NotNull File file) {
            this.file = file;
        }

        @Override
        protected byte[] compute() {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            if (file.isFile()) {
                return hashFile(file);
            }
            md.digest(file.getName().getBytes());
            var children = file.listFiles();
            var tasks = new ArrayList<HashTask>();
            if (children != null) {
                for (var child : children) {
                    var task = new HashTask(child);
                    task.fork();
                    tasks.add(task);
                }
            }
            for (var task : tasks) {
                md.update(task.join());
            }
            return md.digest();
        }
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
