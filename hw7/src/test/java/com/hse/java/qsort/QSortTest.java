package com.hse.java.qsort;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QSortTest {
    private Random random;

    private final int SMALL_SIZE = 25;
    private final int MEDIUM_SIZE = 20000;
    private final int BIG_SIZE = 500000;
    private final int HUGE_SIZE = 4000000;

    @BeforeEach
    void init() {
        random = new Random(17);
    }

    @Test
    void emptyArrayTestSort() {
        testSort(0);
    }

    @Test
    void smallTestSort() {
        testSort(SMALL_SIZE);
    }

    @Test
    void mediumTestSort() {
        testSort(MEDIUM_SIZE);
    }

    @Test
    void bigTestSort() {
        testSort(BIG_SIZE);
    }

    @Test
    void findLowerBoundOfArraySizeWhenParallelSortIsFaster() {
        Integer[] array;
        int left = 1;
        int right = HUGE_SIZE;
        while (left < right - 1) {
            int medium = (left + right) / 2;
            array = makeRandomIntegerArray(medium);
            long timeOneThread = findTimeOfSort(array);
            long timeSeveralThreads = findTimeOfParallelSort(array);
            if (timeOneThread < timeSeveralThreads) {
                left = medium;
            } else {
                right = medium;
            }
        }
        System.out.println("If there is an array of " + left + " Integers, then parallel sort is faster.");
    }

    private long findTimeOfParallelSort(@NotNull Integer[] array) {
        var copyArray = array.clone();
        long startTime = System.currentTimeMillis();
        QSort.qSortParallel(copyArray);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private long findTimeOfSort(@NotNull Integer[] array) {
        var copyArray = array.clone();
        long startTime = System.currentTimeMillis();
        QSort.qSort(copyArray);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private void testSort(int size) {
        var array = makeRandomIntegerArray(size);
        QSort.qSortParallel(array);
        assertTrue(isSorted(array));
    }

    @NotNull
    private Integer[] makeRandomIntegerArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt();
        }
        return array;
    }

    private boolean isSorted(@NotNull Integer[] array) {
        if (array.length <= 1) {
            return true;
        }
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i - 1]) {
                return false;
            }
        }
        return true;
    }


    @Test
    void emptyIntArrayTestSort() {
        testSortInt(0);
    }

    @Test
    void smallIntTestSort() {
        testSortInt(SMALL_SIZE);
    }

    @Test
    void mediumIntTestSort() {
        testSortInt(MEDIUM_SIZE);
    }

    @Test
    void bigIntTestSort() {
        testSortInt(BIG_SIZE);
    }

    @Test
    void findLowerBoundOfIntArraySizeWhenParallelSortIsFaster() {
        int[] array;
        int left = 1;
        int right = HUGE_SIZE;
        while (left < right - 1) {
            int medium = (left + right) / 2;
            array = makeRandomIntArray(medium);
            long timeOneThread = findTimeOfSort(array);
            long timeSeveralThreads = findTimeOfParallelSort(array);
            if (timeOneThread < timeSeveralThreads) {
                left = medium;
            } else {
                right = medium;
            }
        }
        System.out.println("If there is an array of " + left + " ints, then parallel sort is faster.");
    }

    private long findTimeOfParallelSort(@NotNull int[] array) {
        var copyArray = array.clone();
        long startTime = System.currentTimeMillis();
        QSort.qSortParallel(copyArray);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private long findTimeOfSort(@NotNull int[] array) {
        var copyArray = array.clone();
        long startTime = System.currentTimeMillis();
        QSort.qSort(copyArray);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private void testSortInt(int size) {
        var array = makeRandomIntArray(size);
        QSort.qSortParallel(array);
        assertTrue(isSorted(array));
    }

    @NotNull
    private int[] makeRandomIntArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt();
        }
        return array;
    }

    private boolean isSorted(@NotNull int[] array) {
        if (array.length <= 1) {
            return true;
        }
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i - 1]) {
                return false;
            }
        }
        return true;
    }
}
