package com.hse.java.reflector.testClasses;

@SuppressWarnings("ALL")
public class ClassWithConstructors {
    ClassWithConstructors() { }

    public ClassWithConstructors(int x, int y) { }

    protected ClassWithConstructors(int t) { }

    private ClassWithConstructors(ClassWithConstructors other) { }
}
