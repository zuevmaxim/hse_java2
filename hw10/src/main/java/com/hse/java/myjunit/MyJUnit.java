package com.hse.java.myjunit;

import com.hse.java.myjunit.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MyJUnit {
    private Class<?> clazz;
    private List<Method> before;
    private List<Method> beforeClass;
    private List<Method> after;
    private List<Method> afterClass;
    private List<Method> ignoredTests;
    private List<Method> testsWithoutException;
    private List<Method> testsWithException;
    private Object instance;

    public MyJUnit(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        this.clazz = clazz;
        instance = clazz.newInstance();
        before = getMethodsWithAnnotation(Before.class);
        beforeClass = getMethodsWithAnnotation(BeforeClass.class);
        after = getMethodsWithAnnotation(After.class);
        afterClass = getMethodsWithAnnotation(AfterClass.class);
        var tests = getMethodsWithAnnotation(Test.class);
        ignoredTests = tests
                .stream()
                .filter(method -> !method.getAnnotation(Test.class).ignore().equals(""))
                .collect(Collectors.toList());
        tests.removeAll(ignoredTests);
        testsWithoutException = tests
                .stream()
                .filter(method -> method.getAnnotation(Test.class).expected() == Test.DefaultException.class)
                .collect(Collectors.toList());
        tests.removeAll(testsWithoutException);
        testsWithException = tests;
    }

    public List<TestMethodResult> runTests() throws MyJUnitException {
        invokeBeforeAfterMethods(beforeClass);

        var result = new LinkedList<TestMethodResult>();
        ignoredTests.forEach(method ->
                result.add(new TestMethodResult(
                        method.getName(),
                        true,
                        method.getAnnotation(Test.class).ignore(),
                        method.getAnnotation(Test.class).expected(),
                        null
                )));
        var testsWithoutExceptionResults = testsWithoutException.parallelStream().map(method -> new TestMethodResult(
                method.getName(),
                false,
                null,
                null,
                testMethod(method)
        )).collect(Collectors.toList());

        var testsWithExceptionResults = testsWithException.parallelStream().map(method -> {
            var expected = method.getAnnotation(Test.class).expected();
            return new TestMethodResult(
                    method.getName(),
                    false,
                    null,
                    expected,
                    testMethod(method, expected)
            );
        }).collect(Collectors.toList());

        invokeBeforeAfterMethods(afterClass);

        result.addAll(testsWithExceptionResults);
        result.addAll(testsWithoutExceptionResults);
        return result;
    }

    private void checkZeroArguments(Method method) throws MyJUnitException {
        if (method.getParameterTypes().length > 0) {
            throw new MyJUnitException("Methods should not have arguments, but method " + method.getName() + " needs " + method.getParameterTypes().length + " arguments.");
        }
    }

    private void invokeBeforeAfterMethods(List<Method> methods) throws MyJUnitException {
        for (var method : methods) {
            method.setAccessible(true);
            checkZeroArguments(method);
            try {
                method.invoke(instance);
            } catch (IllegalAccessException ignored) {
            } catch (InvocationTargetException e) {
                throw new MyJUnitException("Unexpected exception "
                        + e.getTargetException().getClass().getName()
                        + " occurred in non-test method " + method.getName() + ".");
            }
        }
    }

    public static class TestMethodResult {
        private String name;
        private boolean ignored;
        private String reason;
        private Class<? extends Throwable> expected;
        private RunResult runResult;

        public TestMethodResult(String name, boolean ignored, String reason, Class<? extends Throwable> expected, RunResult runResult) {
            this.name = name;
            this.ignored = ignored;
            this.reason = reason;
            this.expected = expected;
            this.runResult = runResult;
        }

        public String getName() {
            return name;
        }

        public boolean isIgnored() {
            return ignored;
        }

        public String getReason() {
            return reason;
        }

        public Class<? extends Throwable> getExpected() {
            return expected;
        }

        public RunResult getRunResult() {
            return runResult;
        }
    }

    public static class RunResult {
        private boolean passed;
        private Throwable exception;
        private long time;

        public RunResult(boolean passed, Throwable exception, long time) {
            this.passed = passed;
            this.exception = exception;
            this.time = time;
        }

        public boolean isPassed() {
            return passed;
        }

        public Throwable getException() {
            return exception;
        }

        public long getTime() {
            return time;
        }
    }

    private RunResult testMethod(Method method) {
        try {
            invokeBeforeAfterMethods(before);
            method.setAccessible(true);
            checkZeroArguments(method);
            Throwable exception = null;
            var time = System.currentTimeMillis();
            try {
                method.invoke(instance);
            } catch (InvocationTargetException e) {
                exception = e.getTargetException();
            } catch (IllegalAccessException ignored) {
                throw new AssertionError();
            }
            time = System.currentTimeMillis() - time;
            invokeBeforeAfterMethods(after);
            if (exception == null) {
                return new RunResult(true, null, time);
            } else {
                return new RunResult(false, exception, time);
            }
        } catch (MyJUnitException e) {
            return new RunResult(false, e, 0);
        }
    }

    private RunResult testMethod(Method method, Class<? extends Throwable> expected) {
        try {
            invokeBeforeAfterMethods(before);
            method.setAccessible(true);
            checkZeroArguments(method);
            Throwable exception = null;
            boolean passed;
            var time = System.currentTimeMillis();
            try {
                method.invoke(instance);
                passed = false;
            } catch (InvocationTargetException e) {
                if (e.getTargetException().getClass() == expected) {
                    passed = true;
                } else {
                    passed = false;
                    exception = e.getTargetException();
                }
            } catch (IllegalAccessException ignored) {
                throw new AssertionError();
            }
            time = System.currentTimeMillis() - time;
            invokeBeforeAfterMethods(after);
            return new RunResult(passed, exception, time);
        } catch (MyJUnitException e) {
            return new RunResult(false, e, 0);
        }
    }

    private List<Method> getMethodsWithAnnotation(Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }
}
