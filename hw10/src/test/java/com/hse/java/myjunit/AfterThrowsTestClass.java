package com.hse.java.myjunit;

import com.hse.java.myjunit.annotations.AfterClass;
import com.hse.java.myjunit.annotations.Test;

class AfterThrowsTestClass {
    @AfterClass
    static void init() {
        throw new AssertionError();
    }

    @Test
    void test() {

    }
}
