package com.hse.java.myjunit;

import com.hse.java.myjunit.annotations.*;

public class SimpleTestClass {
    private int x;

    @Before
    void init() {
        x = 0;
    }

    @Test
    void passedTest() {

    }

    @Test
    void failedTest() {
        throw new AssertionError();
    }


    @Test(ignore = "Ignore")
    void ignoredTest() {
        throw new AssertionError();
    }

    @Test(expected = RuntimeException.class)
    void throwTest() {
        throw new RuntimeException();
    }

    @Test(expected = RuntimeException.class)
    void notThrowTest() {

    }

    @Test
    void afterFailsTest() {
        x = 1;
    }

    @Test
    void failedArgumentsTest(int z) {
        x = z;
    }

    @After
    void after() {
        if (x == 1) {
            throw new AssertionError();
        }
    }


}
