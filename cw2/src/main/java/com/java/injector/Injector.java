package com.java.injector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Injector {
    private static final Map<Class<?>, Object> objects = new HashMap<>();
    private static final List<Class<?>> tasks = new ArrayList<>();
    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(String rootClassName, List<String> implementationClassNames)
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

    private static Object build(Class<?> root, List<Class<?>> implementations)
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

    private static Class<?> findImplementation(Class<?> abstractClass, List<Class<?>> implementations)
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
