package com.hse.java.reflector.testClasses;

@SuppressWarnings("ALL")
public class ClassWithMethodOverload {
    public static void foo() { }

    private int foo(int x) {
        return 0;
    }

    protected int foo(int x, int y) {
        return 0;
    }

    boolean foo(boolean t) {
        return false;
    }
}
