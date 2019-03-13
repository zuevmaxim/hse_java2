package com.hse.java.qsort;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QSortTest {
    private Random random;

    private final int SMALL_SIZE = 25;
    private final int MEDIUM_SIZE = 20000;
    private final int BIG_SIZE = 500000;

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
    void emptyArrayTestParallelSort() throws InterruptedException {
        testParallelSort(0);
    }

    @Test
    void smallTestParallelSort() throws InterruptedException {
        testParallelSort(SMALL_SIZE);
    }

    @Test
    void mediumTestParallelSort() throws InterruptedException {
        testParallelSort(MEDIUM_SIZE);
    }

    @Test
    void bigTestParallelSort() throws InterruptedException {
        testParallelSort(BIG_SIZE);
    }


    @Test
    void findLowerBoundOfArraySizeWhenParallelSortIsFaster() throws InterruptedException {
        Integer[] array;
        final int HUGE_SIZE = 1000000;
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
        if (left < HUGE_SIZE - 1) {
            System.out.println("If there is an array of " + left + " Integers, then parallel sort is faster.");
        } else {
            System.out.println("Parallel sort is slower on an array of " + HUGE_SIZE +  " elements.");
        }
    }

    private long findTimeOfParallelSort(@NotNull Integer[] array) throws InterruptedException {
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

    private void testParallelSort(int size) throws InterruptedException {
        var array = makeRandomIntegerArray(size);
        var copyArray = array.clone();
        QSort.qSortParallel(array);
        Arrays.sort(copyArray);
        assertArrayEquals(array, copyArray);
    }

    private void testSort(int size) {
        var array = makeRandomIntegerArray(size);
        var copyArray = array.clone();
        QSort.qSort(array);
        Arrays.sort(copyArray);
        assertArrayEquals(array, copyArray);
    }

    @NotNull
    private Integer[] makeRandomIntegerArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt();
        }
        return array;
    }
}