package com.hse.java.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * ThreadPool implementation provides ability to execute
 * parallel tasks with fixed number of threads.
 */
public class ThreadPool {
    /** Array of working threads. */
    private final Worker[] threads;
    /** Queue of tasks to execute. */
    private final Queue<Task> tasks = new LinkedList<>();
    /** Flag shows if thread pool work should be terminated. */
    private volatile boolean isTerminated = false;

    /**
     * Thread pool constructor.
     * @param numberOfThreads the number of working threads. Should be at least one.
     * @throws IllegalArgumentException if numberOfThreads <= 0
     */
    public ThreadPool(int numberOfThreads) throws IllegalArgumentException {
        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException("Number of threads should be at lest one.");
        }
        threads = new Worker[numberOfThreads];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Worker();
            threads[i].start();
        }
    }

    /**
     * Add a task to the thread pool.
     * @param supplier task to execute
     * @param <T> type of the result
     * @return LightFuture object that will contain result,
     * when task will be done.
     * If shutdown() method had been called before, returns null.
     */
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

    /**
     * Terminate thread pool working.
     * New tasks cannot be submitted, but the others will be executed.
     * */
    public void shutdown() {
        for (Worker thread : threads) {
            thread.interrupt();
        }
        synchronized (tasks) {
            tasks.notifyAll();
        }
        isTerminated = true;
    }

    /**
     * Worker is a class, implements working process
     * of a thread from thread pool.
     * Every worker is waiting for tasks in the queue and executes them.
     */
    private class Worker extends Thread {
        /** Thread working cycle. */
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

    /**
     * Task that workers execute.
     * @param <T> type of the result
     */
    private static class Task<T> implements Runnable {
        /** Task to execute. */
        private final Supplier<T> supplier;
        /** Save the result when is ready. */
        private final LightFuture<T> future;

        /** Task constructor. */
        private Task(@NotNull Supplier<T> supplier, @NotNull LightFuture<T> future) {
            this.supplier = supplier;
            this.future = future;
        }

        /**
         * Task execution and saving the result as a LightFuture object.
         * If an exception occurs, it is saved to LightFuture object
         * and will be thrown when get() method will be called.
         */
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

    /**
     * A container for the result of a task,
     * that will be saved when task will be executed.
     * @param <T> type of the result
     */
    public class LightFuture<T> {
        /** A flag if the task is executed. */
        private volatile boolean isReady = false;
        /** Result value. */
        private T result;
        /** Exception that occurs while task execution. */
        private RuntimeException exception;

        /** Return true iff task is executed. */
        public boolean isReady() {
            return isReady;
        }

        /**
         * Get the result when is ready.
         * @return task execution result
         * @throws LightExecutionException if an exception occurs
         * while task execution.
         */
        public T get() throws LightExecutionException {
            while (!isReady) {
                synchronized (this) {
                    if (!isReady) {
                        try {
                            wait();
                        } catch (InterruptedException ignored) { }
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

        /**
         * Create new task, which evaluates
         * function from a result of previous task.
         * @param function function to execute
         * @param <R> type of a result of a new task
         * @return LightFuture object that will contain result
         */
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

    /**
     * LightExecutionException contains exception
     * that occurs while task execution as suppressed.
     */
    public static class LightExecutionException extends Exception { }
}
