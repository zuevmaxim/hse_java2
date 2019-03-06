package com.java.injector;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Provides static initialize method for creating objects and resolve dependencies;
 */
public class Injector {
    /**
     * Objects that have been built.
     */
    private static final Map<Class<?>, Object> objects = new HashMap<>();
    /**
     * Classes that should be built.
     * If a class is added twice then there is a cycle dependency.
     */
    private static final Set<Class<?>> tasks = new HashSet<>();
    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(@NotNull String rootClassName, @NotNull List<String> implementationClassNames)
            throws ClassNotFoundException, InjectionCycleException, InvocationTargetException,
            InstantiationException, IllegalAccessException, ImplementationNotFoundException,
            AmbiguousImplementationException {
        tasks.clear();
        objects.clear();
        var root = Class.forName(rootClassName);
        var implementations = new ArrayList<Class<?>>();
        for (var implementationName : implementationClassNames) {
            implementations.add(Class.forName(implementationName));
        }
        return build(root, implementations);
    }

    /**
     * Recursively build object resolving dependencies.
     * @param root class of object to build
     * @param implementations given implementations for constructor arguments
     * @return object of root class
     * @throws InjectionCycleException if there is a cycle dependency
     * @throws ImplementationNotFoundException if vital class was not given
     * @throws AmbiguousImplementationException if several classes implements the same interface
     */
    private static Object build(@NotNull Class<?> root, @NotNull List<Class<?>> implementations)
            throws InjectionCycleException, IllegalAccessException, InvocationTargetException,
            InstantiationException, ImplementationNotFoundException, AmbiguousImplementationException {
        if (objects.containsKey(root)) {
            return objects.get(root);
        }
        if (tasks.contains(root)) {
            throw new InjectionCycleException();
        }
        tasks.add(root);
        var constructor = root.getDeclaredConstructors()[0];
        var dependencies = constructor.getParameterTypes();
        var parameterObjects = new ArrayList<>();
        for (var dependency : dependencies) {
            Class<?> parameterClass = null;
            var modifier = dependency.getModifiers();
            if (Modifier.isAbstract(modifier) || Modifier.isInterface(modifier)) {
                parameterClass = findImplementation(dependency, implementations);
            } else {
                parameterClass = dependency;
            }
            parameterObjects.add(build(parameterClass, implementations));
        }
        Object result = constructor.newInstance(parameterObjects.toArray());
        objects.put(root, result);
        return result;
    }

    /**
     * Find class implementing 'abstractClass'.
     * @param abstractClass interface or abstract class
     * @param implementations given implementations
     * @return class implementing 'abstractClass'
     * @throws AmbiguousImplementationException no implementing class found
     * @throws ImplementationNotFoundException if several classes implements the same interface
     */
    private static Class<?> findImplementation(@NotNull Class<?> abstractClass, @NotNull List<Class<?>> implementations)
            throws AmbiguousImplementationException, ImplementationNotFoundException {
        Class<?> result = null;
        for (var implementation : implementations) {
            var modifier = implementation.getModifiers();
            if (Modifier.isAbstract(modifier) || Modifier.isInterface(modifier)) {
                continue;
            }
            if (abstractClass.isAssignableFrom(implementation)) {
                if (result == null) {
                    result = implementation;
                } else {
                    throw new AmbiguousImplementationException();
                }
            }
        }
        if (result == null) {
            throw new ImplementationNotFoundException();
        }
        return result;
    }
}
