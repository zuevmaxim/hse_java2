package com.hse.java.qsort;

import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class provides static methods for quick sorting.
 * Parallel sort is possible using qSortParallel method.
 */
public class QSort {
    /**
     * Constructor is private because all the methods are static.
     */
    private QSort() { }

    /**
     * Random generator.
     * Needed for quick sort algorithm.
     */
    private static final Random GENERATOR = new Random();

    /**
     * Sorting an array using one thread.
     * @param array array to sort
     * @param <T> type of array elements, should extend Comparable
     */
    public static <T extends Comparable<? super T>> void qSort(
            @NotNull T[] array) {
        qSortRecursive(array, 0, array.length);
    }

    /**
     * Recursive sort a segment.
     * @param array array to sort
     * @param segmentStart start of a segment
     * @param segmentEnd end of a segment
     * @param <T> type of array elements
     */
    private static <T extends Comparable<? super T>> void qSortRecursive(
            @NotNull T[] array,
            int segmentStart,
            int segmentEnd) {
        if (segmentEnd <= segmentStart + 1) {
            return;
        }
        int center = partition(array, segmentStart, segmentEnd,
                GENERATOR.nextInt(segmentEnd - segmentStart) + segmentStart);
        qSortRecursive(array, segmentStart, center);
        qSortRecursive(array, center, segmentEnd);
    }

    /**
     * Make a partition of an array.
     * As a result array is divided into two parts : elements that are smaller
     * then array[index] element and all the others.
     * @param array array to make partition
     * @param segmentStart start of a segment
     * @param segmentEnd end of a segment
     * @param index index of an element to be central in a resulting array
     * @param <T> type of array elements, should extend Comparable
     * @return index of start of the group of elements that are greater then central one
     */
    private static <T extends Comparable<? super T>> int partition(
            @NotNull T[] array,
            int segmentStart,
            int segmentEnd,
            int index) {
        T element = array[index];
        int i = segmentStart;
        int j = segmentEnd - 1;
        while (i <= j) {
            while (array[i].compareTo(element) < 0) {
                i++;
            }
            while (array[j].compareTo(element) > 0) {
                j--;
            }
            if (i <= j) {
                T tmp = array[i];
                array[i] = array[j];
                array[j] = tmp;
                i++;
                j--;
            }
        }
        return i;
    }

    /**
     * Sorting an array using multiply threads.
     * Number of threads equals to Runtime.getRuntime().availableProcessors()
     * @param array array to sort
     * @param <T>  type of array elements, should extend Comparable
     */
    public static <T extends Comparable<? super T>> void qSortParallel(
            @NotNull T[] array) throws InterruptedException {
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        AtomicInteger currentNumberOfTasks = new AtomicInteger(1);
        executor.execute(new SortSegmentTask<>(executor, currentNumberOfTasks, array, 0, array.length));
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (currentNumberOfTasks) {
            while (currentNumberOfTasks.get() > 0) {
                currentNumberOfTasks.wait();
            }
        }
        executor.shutdown();
    }

    private static class SortSegmentTask<T extends Comparable<? super T>>
            implements Runnable {
        /** Tasks executor. */
        private final ExecutorService executor;
        /** Current number of rest tasks. Flag for task execution finishing. */
        private final AtomicInteger currentNumberOfTasks;
        /** Array to sort. */
        private final T[] array;
        /** Start of a segment to sort. */
        private final int segmentStart;
        /** End of a segment to sort. */
        private final int segmentEnd;

        /** SortSegmentTask constructor. */
        private SortSegmentTask(ExecutorService executor,
                                AtomicInteger currentNumberOfTasks,
                                T[] array,
                                int segmentStart,
                                int segmentEnd) {
            this.executor = executor;
            this.currentNumberOfTasks = currentNumberOfTasks;
            this.array = array;
            this.segmentStart = segmentStart;
            this.segmentEnd = segmentEnd;
        }

        @Override
        public void run() {
            if (segmentEnd > segmentStart + 1) {
                int center = partition(array, segmentStart, segmentEnd,
                        GENERATOR.nextInt(segmentEnd - segmentStart) + segmentStart);
                currentNumberOfTasks.addAndGet(2);
                executor.execute(new SortSegmentTask<>(executor, currentNumberOfTasks, array, segmentStart, center));
                executor.execute(new SortSegmentTask<>(executor, currentNumberOfTasks,array, center, segmentEnd));
            }
            if (currentNumberOfTasks.decrementAndGet() == 0) {
                synchronized (currentNumberOfTasks) {
                    currentNumberOfTasks.notify();
                }
            }
        }
    }
}
