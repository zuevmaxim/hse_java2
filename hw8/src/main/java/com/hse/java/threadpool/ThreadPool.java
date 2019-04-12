package com.hse.java.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class ThreadPool {
    private final Worker[] threads;
    private final Queue<Task> tasks = new LinkedList<>();

    public ThreadPool(int numberOfThreads) {
        threads = new Worker[numberOfThreads];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Worker();
            threads[i].start();
        }
    }

    @NotNull
    public <T> LightFuture<T> submit(@NotNull Supplier<T> supplier) {
        var future = new LightFuture<T>();
        var task = new Task<>(supplier, future);
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
        return future;
    }

    public void shutdown() {
        for (Worker thread : threads) {
            thread.interrupt();
        }
        synchronized (tasks) {
            tasks.notifyAll();
        }
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            Task<?> task;
            while (!Thread.interrupted()) {
                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    task = tasks.poll();
                }
                task.run();
            }
        }
    }

    private static class Task<T> implements Runnable {
        private final Supplier<T> supplier;
        private final LightFuture<T> future;

        private Task(@NotNull Supplier<T> supplier, @NotNull LightFuture<T> future) {
            this.supplier = supplier;
            this.future = future;
        }

        @Override
        public void run() {
            synchronized (future) {
                try {
                    future.result = supplier.get();
                } catch (RuntimeException e) {
                    future.exception = e;
                }
                future.isReady = true;
                future.notify();
            }
        }
    }

    public static class LightFuture<T> {
        private volatile boolean isReady = false;
        private T result;
        private RuntimeException exception;

        public boolean isReady() {
            return isReady;
        }

        public T get() throws LightExecutionException {
            while (!isReady) {
                synchronized (this) {
                    if (!isReady) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            //TODO
                        }
                    }
                }
            }
            if (exception != null) {
                var futureException = new LightExecutionException();
                futureException.addSuppressed(exception);
                throw futureException;
            }
            return result;
        }
    }

    public static class LightExecutionException extends Exception { }
}
