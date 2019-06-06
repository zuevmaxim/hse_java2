package com.hse.java.myjunit;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MyJUnitTest {

    private MyJUnit myJUnit;

    @Test
    void simpleTest() throws MyJUnitException {
        myJUnit = new MyJUnit(Collections.singletonList(SimpleTestClass.class));
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
                case "throwOther":
                    assertFalse(result.isIgnored());
                    assertNull(result.getReason());
                    assertEquals(RuntimeException.class, result.getExpected());
                    assertFalse(result.getRunResult().isPassed());
                    assertEquals(AssertionError.class, result.getRunResult().getException().getClass());
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
    void severalClassesTest() throws MyJUnitException {
        myJUnit = new MyJUnit(Arrays.asList(SimpleTestClass.class, OtherSimpleTestClass.class));
        var result = myJUnit.runTests();
        var total = result.size();
        var passed = result.stream().filter(res -> res.getRunResult() != null
                                                    && res.getRunResult().isPassed())
                .count();
        var ignored = result.stream().filter(MyJUnit.TestMethodResult::isIgnored).count();
        var failed = result.stream().filter(res -> res.getRunResult() != null
                                                    && !res.getRunResult().isPassed())
                .count();
        assertEquals(16, total);
        assertEquals(4, passed);
        assertEquals(10, failed);
        assertEquals(2, ignored);
    }

    @Test
    void privateConstructorTest() throws MyJUnitException {
        myJUnit = new MyJUnit(Collections.singletonList(PrivateConstructorTestClass.class));
        var result = myJUnit.runTests();
        var testResult = result.get(0);
        assertFalse(testResult.getRunResult().isPassed());
        assertEquals(MyJUnitException.class, testResult.getRunResult().getException().getClass());
    }

    @Test
    void constructorWithParametersTest() throws MyJUnitException {
        myJUnit = new MyJUnit(Collections.singletonList(ConstructorWithParametersTestClass.class));
        var result = myJUnit.runTests();
        var testResult = result.get(0);
        assertFalse(testResult.getRunResult().isPassed());
        assertEquals(MyJUnitException.class, testResult.getRunResult().getException().getClass());
    }

    @Test
    void beforeThrowsTest() {
        myJUnit = new MyJUnit(Collections.singletonList(BeforeThrowsTestClass.class));
        assertThrows(MyJUnitException.class, () -> myJUnit.runTests());
    }

    @Test
    void afterThrowsTest() {
        myJUnit = new MyJUnit(Collections.singletonList(AfterThrowsTestClass.class));
        assertThrows(MyJUnitException.class, () -> myJUnit.runTests());
    }

    @Test
    void returnValueTest() throws MyJUnitException {
        myJUnit = new MyJUnit(Collections.singletonList(ReturnValueTestClass.class));
        var result = myJUnit.runTests();
        var testResult = result.get(0);
        assertFalse(testResult.getRunResult().isPassed());
        assertEquals(MyJUnitException.class, testResult.getRunResult().getException().getClass());
    }

    @Test
    void withParametersTest() throws MyJUnitException {
        myJUnit = new MyJUnit(Collections.singletonList(WithParametersTestClass.class));
        var result = myJUnit.runTests();
        var testResult = result.get(0);
        assertFalse(testResult.getRunResult().isPassed());
        assertEquals(MyJUnitException.class, testResult.getRunResult().getException().getClass());
    }

    @Test
    void nonStaticBeforeAfterClassTest() {
        myJUnit = new MyJUnit(Collections.singletonList(NonStaticBeforeAfterClassTestClass.class));
        assertThrows(MyJUnitException.class, () -> myJUnit.runTests());
    }

    @Test
    void abstractTest() throws MyJUnitException {
        myJUnit = new MyJUnit(Collections.singletonList(AbstractTestClass.class));
        var result = myJUnit.runTests();
        var testResult = result.get(0);
        assertFalse(testResult.getRunResult().isPassed());
        assertEquals(MyJUnitException.class, testResult.getRunResult().getException().getClass());
    }
}