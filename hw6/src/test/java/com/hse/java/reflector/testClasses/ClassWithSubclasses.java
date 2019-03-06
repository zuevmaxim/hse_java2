package com.hse.java.reflector.testClasses;

@SuppressWarnings("ALL")
public class ClassWithSubclasses {
    public static class Nested {
        private int x;

        public static class NestedNested {
            int foo() {
                return 0;
            }
        }
    }
    public class Inner {
        private int c;

        public class InnerInner {

        }
    }
}
