package com.hse.java.myjunit;

import com.hse.java.myjunit.annotations.*;

class OtherSimpleTestClass {
    private int x;

    @BeforeClass
    static void beforeClass() {

    }

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

    @Test(expected = RuntimeException.class)
    void throwOther() {
        throw new AssertionError();
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

    @AfterClass
    static void afterClass() {

    }
}
