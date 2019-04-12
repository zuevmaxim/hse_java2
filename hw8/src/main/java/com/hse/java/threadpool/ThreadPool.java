package com.hse.java.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPool {
    private final Worker[] threads;
    private final Queue<Task> tasks = new LinkedList<>();
    private volatile boolean isTerminated = false;

    public ThreadPool(int numberOfThreads) {
        threads = new Worker[numberOfThreads];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Worker();
            threads[i].start();
        }
    }

    public <T> LightFuture<T> submit(@NotNull Supplier<T> supplier) {
        if (isTerminated) {
            return null;
        }
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
        isTerminated = true;
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            Task<?> task;
            while (!isTerminated || !tasks.isEmpty()) {
                synchronized (tasks) {
                    if (isTerminated && tasks.isEmpty()) {
                        return;
                    }
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

    public class LightFuture<T> {
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
                            var futureException = new LightExecutionException();
                            futureException.addSuppressed(e);
                            throw futureException;
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

        public <R> LightFuture<R> thenApply(Function<T, R> function) {
            return submit(() -> {
                try {
                    return function.apply(get());
                } catch (LightExecutionException e) {
                    throw (RuntimeException) e.getSuppressed()[0];
                }
            });
        }
    }

    public static class LightExecutionException extends Exception { }
}
