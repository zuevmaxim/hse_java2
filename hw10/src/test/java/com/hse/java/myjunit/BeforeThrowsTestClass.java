package com.hse.java.myjunit;

import com.hse.java.myjunit.annotations.BeforeClass;
import com.hse.java.myjunit.annotations.Test;

public class BeforeThrowsTestClass {
    @BeforeClass
    void init() {
        throw new AssertionError();
    }

    @Test
    void test() {

    }
}
