package com.java.injector;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import com.java.injector.testClasses.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;



public class InjectorTest {

    @Test
    public void injectorShouldInitializeClassWithoutDependencies()
            throws Exception {
        Object object = Injector.initialize("com.java.injector.testClasses.ClassWithoutDependencies", Collections.emptyList());
        assertTrue(object instanceof ClassWithoutDependencies);
    }

    @Test
    public void injectorShouldInitializeClassWithOneClassDependency()
            throws Exception {
        Object object = Injector.initialize(
                "com.java.injector.testClasses.ClassWithOneClassDependency",
                Collections.singletonList("com.java.injector.testClasses.ClassWithoutDependencies")
        );
        assertTrue(object instanceof ClassWithOneClassDependency);
        ClassWithOneClassDependency instance = (ClassWithOneClassDependency) object;
        assertNotNull(instance.dependency);
    }

    @Test
    public void injectorShouldInitializeClassWithOneInterfaceDependency()
            throws Exception {
        Object object = Injector.initialize(
                "com.java.injector.testClasses.ClassWithOneInterfaceDependency",
                Collections.singletonList("com.java.injector.testClasses.InterfaceImpl")
        );
        assertTrue(object instanceof ClassWithOneInterfaceDependency);
        ClassWithOneInterfaceDependency instance = (ClassWithOneInterfaceDependency) object;
        assertTrue(instance.dependency instanceof InterfaceImpl);
    }

    @Test
    public void injectorShouldThrowExceptionIfCycleDependency() {
        assertThrows(InjectionCycleException.class,
                () -> Injector.initialize("com.java.injector.testClasses.FirstClassWithCycleDependency",
                        Collections.singletonList("com.java.injector.testClasses.SecondClassWithCycleDependency")));
    }

    @Test
    public void injectorShouldThrowIfNoImplementationGiven() {
        assertThrows(ImplementationNotFoundException.class,
                () -> Injector.initialize(
                        "com.java.injector.testClasses.ClassWithOneInterfaceDependency",
                        Collections.emptyList()));
    }

    @Test
    public void injectorShouldThrowIfAmbiguousImplementation() {
        var implementations = new ArrayList<String>();
        implementations.add("com.java.injector.testClasses.InterfaceImpl");
        implementations.add("com.java.injector.testClasses.AnotherInterfaceImpl");
        assertThrows(AmbiguousImplementationException.class,
                () -> Injector.initialize(
                        "com.java.injector.testClasses.ClassWithOneInterfaceDependency",
                        implementations));
    }

    @Test
    public void injectorShouldThrowIfAmbiguousImplementationEvenWithExtends() {
        var implementations = new ArrayList<String>();
        implementations.add("com.java.injector.testClasses.InterfaceImpl");
        implementations.add("com.java.injector.testClasses.ClassExtendsAnotherInterfaceImpl");
        assertThrows(AmbiguousImplementationException.class,
                () -> Injector.initialize(
                        "com.java.injector.testClasses.ClassWithOneInterfaceDependency",
                        implementations));
    }

    @Test
    public void injectorShouldInitializeClassWithSeveralClassDependency()
            throws IllegalAccessException, AmbiguousImplementationException, InstantiationException,
            ImplementationNotFoundException, InjectionCycleException, InvocationTargetException,
            ClassNotFoundException {
        var implementations = new ArrayList<String>();
        implementations.add("com.java.injector.testClasses.ClassWithOneClassDependency");
        implementations.add("com.java.injector.testClasses.ClassExtendsAnotherInterfaceImpl");
        implementations.add("com.java.injector.testClasses.ClassWithoutDependencies");
        Object object = Injector.initialize(
                "com.java.injector.testClasses.ClassWithSeveralDependencies",
                implementations);
        assertTrue(object instanceof ClassWithSeveralDependencies);
    }
}