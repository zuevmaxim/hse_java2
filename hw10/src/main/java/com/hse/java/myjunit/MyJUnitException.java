package com.hse.java.myjunit;

/**
 * Exception appears if exception occurs while testing.
 */
public class MyJUnitException extends Exception {
    public MyJUnitException(String message) {
        super(message);
    }
}
