package com.hse.java.reflector.testClasses;

@SuppressWarnings("ALL")
public class ClassWithExceptions {
    private static void foo() throws TestException { }
    public int boo() throws AnotherTestException, TestException {
        return 1;
    }

    public ClassWithExceptions() throws AnotherTestException {

    }
}
