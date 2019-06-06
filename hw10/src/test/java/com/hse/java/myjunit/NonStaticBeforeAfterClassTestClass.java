package com.hse.java.myjunit;

import com.hse.java.myjunit.annotations.AfterClass;
import com.hse.java.myjunit.annotations.BeforeClass;
import com.hse.java.myjunit.annotations.Test;

class NonStaticBeforeAfterClassTestClass {
    @BeforeClass
    void init() {

    }

    @Test
    void test() {

    }

    @AfterClass
    void end() {

    }

}
