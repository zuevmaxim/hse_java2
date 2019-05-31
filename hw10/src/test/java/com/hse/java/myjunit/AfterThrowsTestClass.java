package com.hse.java.myjunit;

import com.hse.java.myjunit.annotations.AfterClass;
import com.hse.java.myjunit.annotations.Test;

public class AfterThrowsTestClass {
    @AfterClass
    void init() {
        throw new AssertionError();
    }

    @Test
    void test() {

    }
}
