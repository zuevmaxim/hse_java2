package com.hse.java.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * ThreadPool implementation provides ability to execute
 * parallel tasks with fixed number of threads.
 */
public class ThreadPool {
    /** Array of working threads. */
    private final Thread[] threads;

    /** Queue of tasks to execute. */
    private final Queue<Task> tasks = new LinkedList<>();

    /** Flag shows if thread pool work should be terminated. */
    private final AtomicBoolean isTerminated = new AtomicBoolean(false);

    /**
     * Thread pool constructor.
     * @param numberOfThreads the number of working threads. Should be at least one.
     * @throws IllegalArgumentException if numberOfThreads <= 0
     */
    public ThreadPool(int numberOfThreads) throws IllegalArgumentException {
        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException("Number of threads should be at lest one.");
        }
        threads = new Thread[numberOfThreads];

        Runnable workingCycle = () -> {
            Task<?> task;
            while (!isTerminated.get() || !tasks.isEmpty()) {
                synchronized (tasks) {
                    if (isTerminated.get() && tasks.isEmpty()) {
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
        };

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(workingCycle);
            threads[i].start();
        }
    }

    /**
     * Add a task to the thread pool.
     * @param supplier task to execute
     * @param <T> type of the result
     * @return LightFuture object that will contain result,
     * when task will be done.
     * @throws IllegalStateException if shutdown() method had been called before
     */
    @NotNull
    public <T> LightFuture<T> submit(@NotNull Supplier<T> supplier) throws IllegalStateException {
        synchronized (isTerminated) {
            if (isTerminated.get()) {
                throw new IllegalStateException("Task submitting had been terminated.");
            }
            var future = new LightFuture<T>();
            var task = new Task<>(supplier, future);
            synchronized (tasks) {
                tasks.add(task);
                tasks.notify();
            }
            return future;
        }
    }

    /**
     * Submit a task without creating.
     * @param task task to submit
     */
    private void submit(@NotNull Task<?> task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
    }

    /**
     * Terminate thread pool working.
     * New tasks cannot be submitted, but the others will be executed.
     * Wait until all the submitted tasks will be done.
     * @throws InterruptedException if waiting is interrupted
     */
    public void shutdown() throws InterruptedException {
        synchronized (isTerminated) {
            isTerminated.set(true);
        }

        for (var thread : threads) {
            thread.interrupt();
        }

        synchronized (tasks) {
            tasks.notifyAll();
        }

        for (var thread : threads) {
            thread.join();
        }
    }

    /**
     * Task that threads execute.
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
                synchronized (future.isReady) {
                    future.isReady.set(true);
                    future.submitThenApplyTasks();
                }
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
        private final AtomicBoolean isReady = new AtomicBoolean(false);

        /** Result value. */
        private T result;

        /** Exception that occurs while task execution. */
        private RuntimeException exception;

        /** List of then apply tasks. */
        private List<Task<?>> taskList = new ArrayList<>();

        /** Return true iff task is executed. */
        public boolean isReady() {
            return isReady.get();
        }

        /**
         * Get the result when is ready.
         * @return task execution result
         * @throws LightExecutionException if an exception occurs
         * while task execution.
         */
        public T get() throws LightExecutionException {
            while (!isReady.get()) {
                synchronized (this) {
                    if (!isReady.get()) {
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
        @NotNull
        public <R> LightFuture<R> thenApply(Function<? super T, R> function) {
            Supplier<R> supplier = () -> {
                try {
                    return function.apply(get());
                } catch (LightExecutionException e) {
                    throw (RuntimeException) e.getSuppressed()[0];
                }
            };
            synchronized (isReady) {
                if (isReady.get()) {
                    return submit(supplier);
                }
                var future = new LightFuture<R>();
                taskList.add(new Task<>(supplier, future));
                return future;
            }
        }

        /** Submit all the tasks waiting for this task end. */
        private void submitThenApplyTasks() {
            for (var task : taskList) {
                submit(task);
            }
            taskList = null;
        }
    }

    /**
     * LightExecutionException contains exception
     * that occurs while task execution as suppressed.
     */
    public static class LightExecutionException extends Exception { }
}
