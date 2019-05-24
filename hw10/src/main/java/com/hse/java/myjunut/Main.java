package com.hse.java.myjunut;

public class Main {
    public static void main(String[] args) {
        int total;
        int passed = 0;
        int failed = 0;
        int ignored = 0;
        try {
            var results = new MyJUnit(TestClass.class).runTests();
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
            System.exit(0);
        } catch (IllegalAccessException | InstantiationException e) {
            System.out.println("Test class should have public default constructor.");
        } catch (MyJUnitException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Testing failed.");
        System.exit(1);
    }
}
