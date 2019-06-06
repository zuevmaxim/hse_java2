package com.hse.java.myjunit;

import com.hse.java.myjunit.annotations.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for testing a Class object methods annotated with @Test.
 * All testing classes should have public default constructor.
 * All annotated methods should have zero arguments, return void and be non-abstract.
 * Before/AfterClass methods should be static.
 */
public class MyJUnit {
    /** Classes to test. */
    private final List<Class<?>> classes;

    /** Before methods to invoke. */
    private List<AbstractMap.SimpleEntry<Method, Class<?>>> before;

    /** Methods to invoke before all tests. */
    private List<AbstractMap.SimpleEntry<Method, Class<?>>> beforeClass;

    /** After methods to invoke. */
    private List<AbstractMap.SimpleEntry<Method, Class<?>>> after;

    /** Methods to invoke after all tests. */
    private List<AbstractMap.SimpleEntry<Method, Class<?>>> afterClass;

    /** Tests to ignore. */
    private List<AbstractMap.SimpleEntry<Method, Class<?>>> ignoredTests;

    /** Test that should pass. */
    private List<AbstractMap.SimpleEntry<Method, Class<?>>> testsWithoutException;

    /** Tests throwing exceptions. */
    private List<AbstractMap.SimpleEntry<Method, Class<?>>> testsWithException;


    /**
     * Constructor prepares for testing.
     * @param classes list of classes to test
     */
    public MyJUnit(@NotNull List<Class<?>> classes) {
        this.classes = classes;
        before = getMethodsWithAnnotation(Before.class);
        beforeClass = getMethodsWithAnnotation(BeforeClass.class);
        after = getMethodsWithAnnotation(After.class);
        afterClass = getMethodsWithAnnotation(AfterClass.class);
        var tests = getMethodsWithAnnotation(Test.class);
        ignoredTests = tests
                .stream()
                .filter(pair -> !pair.getKey().getAnnotation(Test.class).ignore().equals(""))
                .collect(Collectors.toList());
        tests.removeAll(ignoredTests);
        testsWithoutException = tests
                .stream()
                .filter(pair -> pair.getKey().getAnnotation(Test.class).expected() == Test.DefaultException.class)
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
    @NotNull
    public List<TestMethodResult> runTests() throws MyJUnitException {
        invokeBeforeAfterClassMethods(beforeClass);

        var result = new LinkedList<TestMethodResult>();
        ignoredTests.forEach(pair -> {
                var method = pair.getKey();
                result.add(new TestMethodResult(
                        method.getName(),
                        true,
                        method.getAnnotation(Test.class).ignore(),
                        method.getAnnotation(Test.class).expected(),
                        null
                ));
        });
        var testsWithoutExceptionResults = testsWithoutException.parallelStream().map(pair -> {
            var method = pair.getKey();
            var clazz = pair.getValue();
            return new TestMethodResult(
                    method.getName(),
                    false,
                    null,
                    null,
                    testMethod(method, clazz)
            );
        }).collect(Collectors.toList());

        var testsWithExceptionResults = testsWithException.parallelStream().map(pair -> {
            var method = pair.getKey();
            var clazz = pair.getValue();
            var expected = method.getAnnotation(Test.class).expected();
            return new TestMethodResult(
                    method.getName(),
                    false,
                    null,
                    expected,
                    testMethod(method, clazz, expected)
            );
        }).collect(Collectors.toList());

        invokeBeforeAfterClassMethods(afterClass);

        result.addAll(testsWithExceptionResults);
        result.addAll(testsWithoutExceptionResults);
        return result;
    }

    /**
     * Check that method has no arguments, returns void and non abstract or throw otherwise.
     * @param method method to check
     * @throws MyJUnitException if method cannot be invoked
     */
    private void checkMethod(@NotNull Method method) throws MyJUnitException {
        if (method.getParameterTypes().length > 0) {
            throw new MyJUnitException("Methods should not have arguments, but method " + method.getName() + " needs " + method.getParameterTypes().length + " arguments.");
        }
        if (Modifier.isAbstract(method.getModifiers())) {
            throw new MyJUnitException("Methods should not be abstract, but " + method.getName() + " is.");
        }
        if (method.getReturnType() != void.class) {
            throw new MyJUnitException("Methods should return void, but " + method.getName() + " returns " + method.getReturnType().getName() + ".");
        }
    }

    /**
     * Invoke methods before or after test.
     * @param methods methods to invoke
     * @param instance instance of the test class
     * @throws MyJUnitException if an error occurs while running
     */
    private void invokeBeforeAfterMethods(@NotNull List<AbstractMap.SimpleEntry<Method, Class<?>>> methods,
                                          @Nullable Object instance) throws MyJUnitException {
        for (var pair : methods) {
            if (instance != null && pair.getValue() != instance.getClass()) {
                continue;
            }
            var method = pair.getKey();
            method.setAccessible(true);
            checkMethod(method);
            try {
                method.invoke(instance);
            } catch (IllegalAccessException ignored) {
                throw new AssertionError();
            } catch (InvocationTargetException e) {
                throw new MyJUnitException("Unexpected exception "
                        + e.getTargetException().getClass().getName()
                        + " occurred in non-test method " + method.getName() + ".");
            }
        }
    }

    /**
     * Invoke methods before or after test.
     * @param methods methods to invoke, should be static
     * @throws MyJUnitException if an error occurs while running
     */
    private void invokeBeforeAfterClassMethods(@NotNull List<AbstractMap.SimpleEntry<Method, Class<?>>> methods)
            throws MyJUnitException {
        for (var pair : methods) {
            if (!Modifier.isStatic(pair.getKey().getModifiers())) {
                throw new MyJUnitException("Before/AfterClass methods should be static, but " + pair.getKey().getName() + " is not.");
            }
        }
        invokeBeforeAfterMethods(methods, null);
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
    @NotNull
    private RunResult testMethod(@NotNull Method method, @NotNull Class<?> clazz) {
        try {
            var instance = clazz.newInstance();
            invokeBeforeAfterMethods(before, instance);
            method.setAccessible(true);
            checkMethod(method);
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
            invokeBeforeAfterMethods(after, instance);
            if (exception == null) {
                return new RunResult(true, null, time);
            } else {
                return new RunResult(false, exception, time);
            }
        } catch (MyJUnitException e) {
            return new RunResult(false, e, 0);
        } catch (IllegalAccessException | InstantiationException e) {
            return new RunResult(false, new MyJUnitException("Testing class should have public default constructor."), 0);
        }
    }

    /** Test one method that should throw exception. */
    @NotNull
    private RunResult testMethod(@NotNull Method method,
                                 @NotNull Class<?> clazz,
                                 @NotNull Class<? extends Throwable> expected) {
        try {
            var instance = clazz.newInstance();
            invokeBeforeAfterMethods(before, instance);
            method.setAccessible(true);
            checkMethod(method);
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
            invokeBeforeAfterMethods(after, instance);
            return new RunResult(passed, exception, time);
        } catch (MyJUnitException e) {
            return new RunResult(false, e, 0);
        } catch (IllegalAccessException | InstantiationException e) {
            return new RunResult(false, new MyJUnitException("Testing class should have public default constructor."), 0);
        }
    }

    /** List method annotated with an annotation. */
    @NotNull
    private List<AbstractMap.SimpleEntry<Method, Class<?>>> getMethodsWithAnnotation(
            @NotNull Class<? extends Annotation> annotation) {
        return classes.stream().flatMap(clazz ->
                    Arrays.stream(clazz.getDeclaredMethods())
                    .map(method -> new AbstractMap.SimpleEntry<Method, Class<?>>(method, clazz)))
                .filter(pair -> pair.getKey().isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }
}
