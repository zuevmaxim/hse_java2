package com.hse.java.myjunit;

import com.hse.java.myjunit.annotations.Test;

class WithParametersTestClass {

    @Test
    void get2(int x) {
        if (x == 2) {
            System.out.println("Two is my favourite number!");
        } else {
            throw new AssertionError();
        }
    }
}
