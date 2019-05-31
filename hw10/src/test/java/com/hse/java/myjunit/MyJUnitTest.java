package com.hse.java.myjunit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MyJUnitTest {
    @BeforeEach
    void init(int t) {
        t += 4;
    }

    @BeforeEach
    void init2() {

    }

    @Test
    void mySuperTest() {

    }

}