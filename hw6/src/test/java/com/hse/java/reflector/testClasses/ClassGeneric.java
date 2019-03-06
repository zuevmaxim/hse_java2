package com.hse.java.reflector.testClasses;

@SuppressWarnings("ALL")
public class ClassGeneric<T, U, K> {
    private T t;
    private U u;
    private K k;

    public T getT() {
        return t;
    }

    public U getU() {
        return u;
    }

    public K getK() {
        return k;
    }

    public void setT(T t) {
        this.t = t;
    }

    public void setU(U u) {
        this.u = u;
    }

    public void setK(K k) {
        this.k = k;
    }

    public void foo(GenericSubClass<? super T> a, GenericSubClass<U> b, GenericSubClass<? extends U> c) {

    }

    public static class GenericSubClass<S> {

    }
}
