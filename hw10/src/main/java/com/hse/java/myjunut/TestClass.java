package com.hse.java.myjunut;

import com.hse.java.myjunut.annotations.AfterClass;
import com.hse.java.myjunut.annotations.Before;
import com.hse.java.myjunut.annotations.BeforeClass;
import com.hse.java.myjunut.annotations.Test;

public class TestClass {
    //private TestClass() {}
    @BeforeClass
    void kek() {
        //assert false;
    }

    @AfterClass
    void kek2() {
        //assert false;
    }

    @Before
    void before() {
    }


    @Test(ignore = "Kek")
    void mySuperTest() {
        assert false;
    }

    @Test
    void mySuperPuperTest() {
        String s = "";
        for (int i = 0; i < 1000; ++i) {
            s += "kek";
        }
    }

    @Test(expected = ArithmeticException.class)
    void myExceptionTest(int x) {
        throw new ArithmeticException();
    }

    @Test(expected = AssertionError.class)
    void myExceptionTest2() {

    }
}
