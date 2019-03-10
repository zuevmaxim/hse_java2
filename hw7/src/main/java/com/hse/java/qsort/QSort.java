package com.hse.java.qsort;

import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Class provides static methods for quick sorting.
 * Realized functions for sorting int arrays or generic-type arrays.
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
    @SuppressWarnings("Duplicates")
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
     * Sorting an array using multiply threads.
     * @param array array to sort
     * @param <T>  type of array elements, should extend Comparable
     */
    public static <T extends Comparable<? super T>> void qSortParallel(
            @NotNull T[] array) {
        var pool = new ForkJoinPool();
        pool.invoke(new SortSegmentTask<>(array, 0, array.length));
    }

    /**
     * Class implements a task that should sort a segment of an array.
     * @param <T> type of array elements, should extend Comparable
     */
    private static class SortSegmentTask<T extends Comparable<? super T>>
            extends RecursiveAction {
        /** Array to sort. */
        private final T[] array;
        /** Start of a segment to sort. */
        private final int segmentStart;
        /** End of a segment to sort. */
        private final int segmentEnd;

        /** Construct SortSegmentTask. */
        private SortSegmentTask(@NotNull T[] array,
                                int segmentStart,
                                int segmentEnd) {
            this.array = array;
            this.segmentStart = segmentStart;
            this.segmentEnd = segmentEnd;
        }

        /**
         * Method for recursive quick sort.
         * Divide a segment into two parts
         * and compute one of them in a new thread.
         */
        @Override
        public void compute() {
            if (segmentEnd > segmentStart + 1) {
                int center = partition(array, segmentStart, segmentEnd,
                        GENERATOR.nextInt(segmentEnd - segmentStart) + segmentStart);
                var leftTask = new SortSegmentTask<>(array, segmentStart, center);
                var rightTask = new SortSegmentTask<>(array, center, segmentEnd);
                leftTask.fork();
                rightTask.compute();
                leftTask.join();
            }
        }

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
     * Sorting an int array using one thread.
     * @param array array to sort
     */
    public static void qSort(@NotNull int[] array) {
        qSortRecursive(array, 0, array.length);
    }

    /**
     * Recursive sort a segment of an int array.
     * @param array array to sort
     * @param segmentStart start of a segment
     * @param segmentEnd end of a segment
     */
    @SuppressWarnings("Duplicates")
    private static void qSortRecursive(@NotNull int[] array,
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
     * Sorting an int array using multiply threads.
     * @param array array to sort
     */
    public static void qSortParallel(@NotNull int[] array) {
        var pool = new ForkJoinPool();
        pool.invoke(new IntSortSegmentTask(array, 0, array.length));
    }

    /**
     * Class implements a task that should sort a segment of an int array.
     */
    private static class IntSortSegmentTask extends RecursiveAction {
        /** Array to sort. */
        private final int[] array;
        /** Start of a segment. */
        private final int segmentStart;
        /** Start of a segment. */
        private final int segmentEnd;

        /** Construct IntSortSegmentTask. */
        private IntSortSegmentTask(@NotNull int[] array,
                                   int segmentStart,
                                   int segmentEnd) {
            this.array = array;
            this.segmentStart = segmentStart;
            this.segmentEnd = segmentEnd;
        }

        /**
         * Method for recursive quick sort.
         * Divide a segment into two parts
         * and compute one of them in a new thread.
         */
        @Override
        public void compute() {
            if (segmentEnd > segmentStart + 1) {
                int center = partition(array, segmentStart, segmentEnd,
                        GENERATOR.nextInt(segmentEnd - segmentStart) + segmentStart);
                var leftTask = new IntSortSegmentTask(array, segmentStart, center);
                var rightTask = new IntSortSegmentTask(array, center, segmentEnd);
                leftTask.fork();
                rightTask.compute();
                leftTask.join();
            }
        }
    }


    /**
     * Make a partition of an int array.
     * As a result array is divided into two parts : elements that are smaller
     * then array[index] element and all the others.
     * @param array array to make partition
     * @param segmentStart start of a segment
     * @param segmentEnd end of a segment
     * @param index index of an element to be central in a resulting array
     * @return index of start of the group of elements that are greater then central one
     */
    private static int partition(
            @NotNull int[] array,
            int segmentStart,
            int segmentEnd,
            int index) {
        int element = array[index];
        int i = segmentStart;
        int j = segmentEnd - 1;
        while (i <= j) {
            while (array[i] < element) {
                i++;
            }
            while (array[j] > element) {
                j--;
            }
            if (i <= j) {
                int tmp = array[i];
                array[i] = array[j];
                array[j] = tmp;
                i++;
                j--;
            }
        }
        return i;
    }
}
