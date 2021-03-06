package com.hse.java.threadpool;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {
    private ThreadPool threadPool;
    private final static Random RANDOM = new Random();
    private final static int LENGTH = 1_000_000;
    private final static int THREAD_NUMBER = 4;
    private final static int TASK_NUMBER = 8;
    private final static List<Supplier<List<Integer>>> listTasks = new ArrayList<>();
    private final static List<Supplier<Integer>> integerTasks = new ArrayList<>();

    @BeforeEach
    void createTasks() {
        listTasks.clear();
        integerTasks.clear();
        for (int i = 0; i < TASK_NUMBER; i++) {
            var list = getRandomIntegerList(LENGTH);
            listTasks.add(() -> {
                Collections.sort(list);
                return list;
            });
        }
        for (int i = 0; i < TASK_NUMBER; i++) {
            var list = getRandomIntegerList(LENGTH);
            listTasks.add(() -> {
                Collections.shuffle(list);
                return list;
            });
        }

        for (int i = 0; i < TASK_NUMBER; i++) {
            var list = getRandomIntegerList(LENGTH);
            integerTasks.add(() -> list.stream().mapToInt(x -> x).sum());
        }

        for (int i = 0; i < TASK_NUMBER; i++) {
            var list = getRandomIntegerList(LENGTH);
            integerTasks.add(() -> Collections.max(list));
        }

    }

    @BeforeEach
    void setUp() {
        threadPool = new ThreadPool(THREAD_NUMBER);
    }

    @Test
    void simpleTestOneTask() throws ThreadPool.LightExecutionException, InterruptedException {
        assertEquals(0, threadPool.submit(() -> 0).get());
        threadPool.shutdown();
    }

    @Test
    void simpleTestManyTasks() throws ThreadPool.LightExecutionException, InterruptedException {
        List<ThreadPool.LightFuture> futures = new ArrayList<>();
        for (int i = 0; i < LENGTH; i++) {
            int j = i;
            futures.add(threadPool.submit(() -> j));
        }
        threadPool.shutdown();
        for (int i = 0; i < LENGTH; ++i) {
            assertEquals(i, futures.get(i).get());
        }
    }

    @Test
    void submitTest() throws ThreadPool.LightExecutionException, InterruptedException {
        var lists = new ArrayList<List<Integer>>();
        for (int j = 0; j < TASK_NUMBER; j++) {
            lists.add(getRandomIntegerList(LENGTH));
        }

        var futures = new ArrayList<ThreadPool.LightFuture<List<Integer>>>();
        for (int i = 0; i < TASK_NUMBER; i++) {
            final int j = i;
            futures.add(threadPool.submit(() -> {
                Collections.sort(lists.get(j));
                return lists.get(j);
            }));
        }
        assertTrue(Thread.activeCount() >= THREAD_NUMBER);
        threadPool.shutdown();

        for (int i = 0; i < TASK_NUMBER; i++) {
            assertTrue(futures.get(i).isReady());
            assertTrue(isSorted(futures.get(i).get()));
            assertTrue(futures.get(i).isReady());
        }
    }

    @Test
    void exceptionTest() throws InterruptedException {
        @SuppressWarnings({"divzero", "NumericOverflow"})
        var future = threadPool.submit(() -> 5 / 0);
        threadPool.shutdown();
        assertThrows(ThreadPool.LightExecutionException.class, future::get);
        try {
            future.get();
        } catch (ThreadPool.LightExecutionException e) {
            assertSame(ArithmeticException.class, e.getSuppressed()[0].getClass());
        }
    }

    @Test
    void exceptionThenApplyTest() throws InterruptedException {
        @SuppressWarnings({"divzero", "NumericOverflow"})
        var future = threadPool.submit(() -> 5 / 0).thenApply(x -> x + 5);
        threadPool.shutdown();
        assertThrows(ThreadPool.LightExecutionException.class, future::get);
        try {
            future.get();
        } catch (ThreadPool.LightExecutionException e) {
            assertSame(ArithmeticException.class, e.getSuppressed()[0].getClass());
        }
    }

    @Test
    void submitTasksAfterShutdown() throws InterruptedException {
        threadPool.shutdown();
        assertThrows(IllegalStateException.class, () -> threadPool.submit(() -> 0));
    }

    @Test
    void thenApplySimpleTest() throws ThreadPool.LightExecutionException, InterruptedException {
        assertEquals(3, threadPool.submit(() -> 0).thenApply(x -> x + 3).get());
        threadPool.shutdown();
    }

    @Test
    void thenApplySequenceSimpleTest() throws ThreadPool.LightExecutionException, InterruptedException {
        var future1 = threadPool.submit(() -> 0);
        var future2 = future1.thenApply(x -> x + 3);
        var future3 = future2.thenApply(x -> x * 3);
        threadPool.shutdown();
        assertEquals(0, future1.get());
        assertEquals(3, future2.get());
        assertEquals(9, future3.get());
    }

    @Test
    void listsSubmitTest() throws ThreadPool.LightExecutionException, InterruptedException {
        var futures = new ArrayList<ThreadPool.LightFuture<List<Integer>>>();
        for (var supplier : listTasks) {
            futures.add(threadPool.submit(supplier));
        }
        assertTrue(Thread.activeCount() >= THREAD_NUMBER);
        threadPool.shutdown();
        for (var future : futures) {
            future.get();
        }
    }

    @Test
    void integerSubmitTest() throws ThreadPool.LightExecutionException, InterruptedException {
        var futures = new ArrayList<ThreadPool.LightFuture<Integer>>();
        for (var supplier : integerTasks) {
            futures.add(threadPool.submit(supplier));
        }
        assertTrue(Thread.activeCount() >= THREAD_NUMBER);
        threadPool.shutdown();
        for (var future : futures) {
            future.get();
        }
    }

    @Test
    void listsApplyTest() throws ThreadPool.LightExecutionException, InterruptedException {
        var futures = new ArrayList<ThreadPool.LightFuture<List<Integer>>>();
        for (var supplier : listTasks) {
            futures.add(threadPool.submit(supplier));
        }
        var applyFutures = new ArrayList<ThreadPool.LightFuture<List<Integer>>>();
        for (var future : futures) {
            applyFutures.add(future.thenApply(integers -> {
                Collections.shuffle(integers);
                return integers;
            }));
        }
        assertTrue(Thread.activeCount() >= THREAD_NUMBER);
        threadPool.shutdown();
        for (var future : applyFutures) {
            future.get();
        }
    }

    @Test
    void oneThreadExecutionTest() throws ThreadPool.LightExecutionException, InterruptedException {
        threadPool = new ThreadPool(1);
        var futures = new ArrayList<ThreadPool.LightFuture<List<Integer>>>();
        for (var supplier : listTasks) {
            futures.add(threadPool.submit(supplier));
        }
        assertTrue(Thread.activeCount() >= 1);
        threadPool.shutdown();
        for (var future : futures) {
            future.get();
        }
    }

    @Test
    void manyThreadsExecutionTest() throws ThreadPool.LightExecutionException, InterruptedException {
        threadPool = new ThreadPool(10);
        var futures = new ArrayList<ThreadPool.LightFuture<List<Integer>>>();
        for (var supplier : listTasks) {
            futures.add(threadPool.submit(supplier));
        }
        assertTrue(Thread.activeCount() >= 10);
        threadPool.shutdown();
        for (var future : futures) {
            future.get();
        }
    }

    @Test
    void zeroThreadExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> new ThreadPool(0));
    }

    @Test
    void shutdownTest() throws InterruptedException {
        var future = threadPool.submit(() -> {
            //noinspection StatementWithEmptyBody
            for (int i = 0; i < LENGTH; i++);
            return 0;
        });
        threadPool.shutdown();
        assertTrue(future.isReady());
    }

    @Contract(pure = true)
    private static <T extends Comparable<? super T>> boolean isSorted(@NotNull List<T> list) {
        T previous = null;
        for (T current : list) {
            if (previous != null && current.compareTo(previous) < 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    private static List<Integer> getRandomIntegerList(@SuppressWarnings("SameParameterValue") int size) {
        var list = new ArrayList<Integer>(size);
        for (int i = 0; i < LENGTH; i++) {
            list.add(RANDOM.nextInt(100));
        }
        return list;
    }
}