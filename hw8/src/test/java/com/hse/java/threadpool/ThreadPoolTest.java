package com.hse.java.threadpool;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {
    private ThreadPool threadPool;
    private final Random random = new Random();
    private final static int LENGTH = 1_000_000;
    private final static int THREAD_NUMBER = 4;
    private final static int TASK_NUMBER = 8;


    @BeforeEach
    void setUp() {
        threadPool = new ThreadPool(THREAD_NUMBER);
    }

    @Test
    void simpleTestOneTask() throws ThreadPool.LightExecutionException {
        assertEquals(0, threadPool.submit(() -> 0).get());
        threadPool.shutdown();
    }

    @Test
    void simpleTestManyTasks() throws ThreadPool.LightExecutionException {
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
    void submitTest() throws ThreadPool.LightExecutionException {
        List<List<Integer>> lists = new ArrayList<>();
        for (int j = 0; j < TASK_NUMBER; j++) {
            lists.add(new ArrayList<>());
            for (int i = 0; i < LENGTH; i++) {
                lists.get(j).add(random.nextInt());
            }
        }

        List<ThreadPool.LightFuture<List<Integer>>> futures = new ArrayList<>();
        for (int i = 0; i < TASK_NUMBER; i++) {
            final int j = i;
            futures.add(threadPool.submit(() -> {
                Collections.sort(lists.get(j));
                return lists.get(j);
            }));
        }
        threadPool.shutdown();

        for (int i = 0; i < TASK_NUMBER; i++) {
            assertTrue(isSorted(futures.get(i).get()));
            assertTrue(futures.get(i).isReady());
        }
    }

    @Test
    void exceptionTest() {
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
    void submitTasksAfterShutdown() {
        threadPool.shutdown();
        assertNull(threadPool.submit(() -> 0));
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
}