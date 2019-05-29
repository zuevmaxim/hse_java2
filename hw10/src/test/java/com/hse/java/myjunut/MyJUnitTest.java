package com.hse.java.myjunut;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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