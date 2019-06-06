package com.hse.java.myjunit;

import com.hse.java.myjunit.annotations.BeforeClass;
import com.hse.java.myjunit.annotations.Test;

class BeforeThrowsTestClass {
    @BeforeClass
    static void init() {
        throw new AssertionError();
    }

    @Test
    void test() {

    }
}
