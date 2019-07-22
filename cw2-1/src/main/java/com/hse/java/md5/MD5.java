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


/**
 * Class for evaluating MD5 hash in a special rule.
 * hash(file) = MD5(file content)
 * hash(dir) = MD5(dir name, hash(file1), ..)
 */
public class MD5 {
    /** Buffer size. */
    private static final int SIZE = 4096;

    /**
     * Calculate hash using one thread.
     * @param file directory or file to find hash from
     * @return byte array hash
     * @throws NoSuchAlgorithmException if no MD5 algorithm found
     */
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

    /**
     * Calculate hash using ForkJoin pool.
     * @param file directory or file to find hash from
     * @return byte array hash
     */
    @NotNull
    public static byte[] hashForkJoin(@NotNull File file) {
        return ForkJoinPool.commonPool().invoke(new HashTask(file));
    }

    /** Task for evaluating hash of one file/directory. */
    private static class HashTask extends RecursiveTask<byte[]> {
        /** Directory or file to find hash from. */
        private File file;

        /** HashTask constructor. */
        private HashTask(@NotNull File file) {
            this.file = file;
        }

        /** Computation function for ForkJoin pool. */
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

    /** Evaluate hash of a regular file. */
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
