package com.hse.java.myjunit;

import com.hse.java.myjunit.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for testing a Class object methods annotated with @Test.
 */
public class MyJUnit {
    /** Class to test. */
    private final Class<?> clazz;

    /** Before methods to invoke. */
    private List<Method> before;

    /** Methods to invoke before all tests. */
    private List<Method> beforeClass;

    /** After methods to invoke. */
    private List<Method> after;

    /** Methods to invoke after all tests. */
    private List<Method> afterClass;

    /** Tests to ignore. */
    private List<Method> ignoredTests;

    /** Test that should pass. */
    private List<Method> testsWithoutException;

    /** Tests throwing exceptions. */
    private List<Method> testsWithException;
    private Object instance;


    /**
     * Constructor prepares for testing.
     * Class should have default public constructor.
     * @param clazz class to test
     * @throws IllegalAccessException if constructor of testing class is private
     * @throws InstantiationException if testing class has no default constructor
     */
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

    /**
     * Run tests annotated with @Test.
     * All the tests and before after methods should not have arguments.
     * @return list of results of the tests
     * @throws MyJUnitException if an error occurred while testing
     */
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

    /**
     * Check that method has no arguments or throw otherwise.
     * @param method method to check
     * @throws MyJUnitException if method has nonzero argument number
     */
    private void checkZeroArguments(Method method) throws MyJUnitException {
        if (method.getParameterTypes().length > 0) {
            throw new MyJUnitException("Methods should not have arguments, but method " + method.getName() + " needs " + method.getParameterTypes().length + " arguments.");
        }
    }

    /**
     * Invoke methods before or after test.
     * @param methods methods to invoke
     * @throws MyJUnitException if an error occurs while running
     */
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

    /** Result of the test. */
    public static class TestMethodResult {
        /** Test name. */
        private final String name;

        /** If test should be ignored. */
        private final boolean ignored;

        /** Reason of ignoring. */
        private final String reason;

        /** Expected exception. */
        private final Class<? extends Throwable> expected;

        /** Result of a runnable test. */
        private final RunResult runResult;

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

    /** Result of a runnable test. */
    public static class RunResult {
        /** If test passed. */
        private final boolean passed;

        /** Exception thrown while running. */
        private final Throwable exception;

        /** Running time of the test. */
        private final long time;

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

    /** Test one method. */
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

    /** Test one method that should throw exception. */
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

    /** List method annotated with an annotation. */
    private List<Method> getMethodsWithAnnotation(Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }
}
