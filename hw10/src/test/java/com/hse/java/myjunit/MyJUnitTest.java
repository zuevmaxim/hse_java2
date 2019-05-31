package com.hse.java.myjunit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyJUnitTest {

    private MyJUnit myJUnit;

    @Test
    void simpleTest() throws InstantiationException, IllegalAccessException, MyJUnitException {
        myJUnit = new MyJUnit(SimpleTestClass.class);
        var results = myJUnit.runTests();
        for (var result : results) {
            switch (result.getName()) {
                case "passedTest":
                    assertFalse(result.isIgnored());
                    assertNull(result.getReason());
                    assertTrue(result.getRunResult().isPassed());
                    assertNull(result.getRunResult().getException());
                    break;
                case "failedTest":
                    assertFalse(result.isIgnored());
                    assertNull(result.getReason());
                    assertFalse(result.getRunResult().isPassed());
                    assertEquals(AssertionError.class, result.getRunResult().getException().getClass());
                    break;
                case "ignoredTest":
                    assertTrue(result.isIgnored());
                    assertEquals("Ignore", result.getReason());
                    assertNull(result.getRunResult());
                    break;
                case "throwTest":
                    assertFalse(result.isIgnored());
                    assertNull(result.getReason());
                    assertEquals(RuntimeException.class, result.getExpected());
                    assertTrue(result.getRunResult().isPassed());
                    assertNull(result.getRunResult().getException());
                    break;
                case "notThrowTest":
                    assertFalse(result.isIgnored());
                    assertNull(result.getReason());
                    assertEquals(RuntimeException.class, result.getExpected());
                    assertFalse(result.getRunResult().isPassed());
                    assertNull(result.getRunResult().getException());
                    break;
                case "afterFailsTest":
                    assertFalse(result.isIgnored());
                    assertNull(result.getReason());
                    assertFalse(result.getRunResult().isPassed());
                    assertEquals(MyJUnitException.class, result.getRunResult().getException().getClass());
                    break;
                case "failedArgumentsTest":
                    assertFalse(result.isIgnored());
                    assertNull(result.getReason());
                    assertFalse(result.getRunResult().isPassed());
                    assertEquals(MyJUnitException.class, result.getRunResult().getException().getClass());
                    break;
            }
        }
    }

    @Test
    void privateConstructorTest() {
        assertThrows(IllegalAccessException.class, () -> new MyJUnit(PrivateConstructorTestClass.class));
    }

    @Test
    void constructorWithParametersTest() {
        assertThrows(InstantiationException.class, () -> new MyJUnit(ConstructorWithParametersTestClass.class));
    }

    @Test
    void beforeThrowsTest() throws InstantiationException, IllegalAccessException {
        myJUnit = new MyJUnit(BeforeThrowsTestClass.class);
        assertThrows(MyJUnitException.class, () -> myJUnit.runTests());
    }

    @Test
    void afterThrowsTest() throws InstantiationException, IllegalAccessException {
        myJUnit = new MyJUnit(AfterThrowsTestClass.class);
        assertThrows(MyJUnitException.class, () -> myJUnit.runTests());
    }

}