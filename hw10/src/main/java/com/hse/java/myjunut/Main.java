package com.hse.java.myjunut;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.JarFile;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Illegal number of arguments. Enter only a path to a .class or .jar file.");
            return;
        }
        var path = args[0];
        if (!path.endsWith(".jar") && !path.endsWith(".class")) {
            System.out.println("Illegal argument. Enter a path to a .class or .jar file.");
            return;
        }
        var file = new File(path);
        if (!file.exists()) {
            System.out.println("No such file: " + file.getName() + ".");
            return;
        }
        var classes = new ArrayList<Class<?>>();
        if (path.endsWith(".class")) {

            String className = file.getName().substring(0, file.getName().indexOf('.'));
            try {
                classes.add(new URLClassLoader(new URL[]{
                        file
                            .toURI()
                            .toURL()
                }).loadClass(className));
            } catch (ClassNotFoundException e) {
                System.out.println("No such class found : " + className + ".");
                return;
            } catch (MalformedURLException e) {
                System.out.println("Error occurred while IO operations: " + e.getMessage());
                return;
            }
        } else {
            try {
                var jar = new JarFile(path);

            } catch (IOException e) {
                System.out.println("Error occurred while IO operations: " + e.getMessage());
                return;
            }
        }


        int total;
        int passed = 0;
        int failed = 0;
        int ignored = 0;
        for (var clazz : classes) {
            try {
                var results = new MyJUnit(clazz).runTests();
                total = results.size();
                for (var result : results) {
                    if (result.isIgnored()) {
                        ignored++;
                        System.out.printf("Test %s ignored. Reason: %s.\n", result.getName(), result.getReason());
                    } else {
                        var runResult = result.getRunResult();
                        var expected = result.getExpected();
                        if (runResult.isPassed()) {
                            passed++;
                            System.out.printf("Test %s passed.\n\tTime : %dms.\n", result.getName(), runResult.getTime());
                        } else {
                            failed++;
                            var exception = runResult.getException();
                            if (exception != null && exception.getClass() == MyJUnitException.class) {
                                System.out.printf("Test failed: %s\n", exception.getMessage());
                            } else {
                                if (expected == null) {
                                    assert exception != null;
                                    System.out.printf("Test %s failed. %s was thrown.\n\tTime : %dms.\n", result.getName(), exception.getClass().getName(), runResult.getTime());
                                } else {
                                    if (exception == null) {
                                        System.out.printf("Test %s failed. %s expected but nothing was thrown.\n\tTime : %dms.\n", result.getName(), expected.getName(), runResult.getTime());
                                    } else {
                                        System.out.printf("Test %s failed. %s expected but %s was thrown.\n\tTime : %dms.\n", result.getName(), expected.getName(), exception.getClass().getName(), runResult.getTime());
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.printf("\nTest result:\n  total   %d\n  passed  %d\n  failed  %d\n  ignored %d\n", total, passed, failed, ignored);
            } catch (IllegalAccessException | InstantiationException e) {
                System.out.println("Test class should have public default constructor.");
            } catch (MyJUnitException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("Testing class " + clazz.getName() + "failed.");
        }
    }
}
