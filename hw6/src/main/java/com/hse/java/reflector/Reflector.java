package com.hse.java.reflector;

import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

/**
 * Class for reflection.
 * Provide ability to print a class information without implementation.
 */
public class Reflector {

    /** Constructor is private because all methods are static. */
    private Reflector() { }

    /**
     * Print class structure without methods implementation.
     * Makes a file `className`.java
     * @param someClass class to print
     * @throws IOException if input-output error occurs
     */
    public static void  printStructure(@NotNull Class<?> someClass)
            throws IOException {
        try (var out = new FileWriter(someClass.getSimpleName() + ".java")) {
            recursivePrintStructure(someClass, out, 0);
        }
    }

    /**
     * Write class information recursively including subclasses.
     * @param someClass class to write
     * @param out file to write
     * @param tabsNumber number of tabs
     * @throws IOException if input-output error occurs
     */
    private static void recursivePrintStructure(
            @NotNull Class<?> someClass,
            @NotNull FileWriter out,
            int tabsNumber) throws IOException {
        var name = someClass.getSimpleName();
        writeModifiers(someClass.getModifiers(), out, tabsNumber);
        out.write("class " + name);
        writeTypeParameters(someClass, out);
        out.write(" ");
        writeExtends(someClass, out);
        writeImplements(someClass, out);
        out.write("{\n");

        for (var clazz : someClass.getDeclaredClasses()) {
            recursivePrintStructure(clazz, out, tabsNumber + 1);
        }

        writeFields(someClass.getDeclaredFields(), out, tabsNumber);
        out.write("\n");

        writeConstructors(someClass.getDeclaredConstructors(), out, tabsNumber);
        out.write("\n");

        writeMethods(someClass.getDeclaredMethods(), out, tabsNumber);

        writeTabs(out, tabsNumber);
        out.write("}\n\n");
    }

    /**
     * Write fields to a file.
     * @param fields fields array to write
     * @param out file to write
     * @param tabsNumber number of tabs
     * @throws IOException if input-output error occurs
     */
    private static void writeFields(@NotNull Field[] fields,
                                    @NotNull FileWriter out,
                                    int tabsNumber)
            throws IOException {
        for (var field : fields) {
            writeModifiers(field.getModifiers(), out, tabsNumber + 1);
            out.write(getType(field.getGenericType()));
            out.write(" " + field.getName());
            if (Modifier.isFinal(field.getModifiers())) {
                out.write(" = " + getDefaultValue(field.getType()));
            }
            out.write(";\n");
        }
    }

    /**
     * Returns a written type string.
     * @param type type to print
     * @return string with type
     */
    @NotNull
    private static String getType(@NotNull Type type) {
        return type.getTypeName().replace('$', '.');
    }

    /**
     * Find a default value for a class.
     * @param clazz type of a value
     * @return "false" for boolean, "0" for primitive types, "null" otherwise
     */
    @NotNull
    private static String getDefaultValue(@NotNull Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == boolean.class) {
                return "false";
            } else {
                return "0";
            }
        }
        return "null";
    }

    /**
     * Write information about constructors.
     * @param constructors class constructors array
     * @param out file to write
     * @param tabsNumber number of tabs
     * @throws IOException if input-output error occurs
     */
    private static void writeConstructors(@NotNull Constructor[] constructors,
                                          @NotNull FileWriter out, int tabsNumber)
            throws IOException {
        for (var constructor : constructors) {
            writeModifiers(constructor.getModifiers(), out, tabsNumber + 1);
            out.write(constructor.getDeclaringClass().getSimpleName() + "(");
            writeParameters(constructor.getParameters(), out);
            out.write(") ");
            writeExceptions(constructor.getGenericExceptionTypes(), out);
            out.write("{ }\n");
        }
    }

    /**
     * Write `throws` information about a method.
     * @param exceptions array of exceptions
     * @param out file to write
     * @throws IOException if input-output error occurs
     */
    private static void writeExceptions(@NotNull Type[] exceptions,
                                        @NotNull FileWriter out)
            throws IOException {
        if (exceptions.length != 0) {
            out.write("throws ");
            out.write(exceptions[0].getTypeName());
            for (int i = 1; i < exceptions.length; i++) {
                out.write(", " + exceptions[i].getTypeName());
            }
            out.write(" ");
        }
    }

    /**
     * Write all methods definitions.
     * @param methods array of class methods
     * @param out file to write
     * @param tabsNumber number of tabs
     * @throws IOException if input-output error occurs
     */
    private static void writeMethods(@NotNull Method[] methods,
                                     @NotNull FileWriter out,
                                     int tabsNumber)
            throws IOException {
        for (var method : methods) {
            writeModifiers(method.getModifiers(), out, tabsNumber + 1);
            out.write(getType(method.getGenericReturnType()) + " ");
            out.write(method.getName() + "(");
            writeParameters(method.getParameters(), out);
            out.write(") ");
            writeExceptions(method.getGenericExceptionTypes(), out);
            if (method.getReturnType() == void.class) {
                out.write("{ }\n");
            } else {
                out.write("{\n");
                writeTabs(out, tabsNumber + 2);
                out.write("return " + getDefaultValue(method.getReturnType()) + ";\n");
                writeTabs(out, tabsNumber + 1);
                out.write("}\n");
            }
        }
    }

    /**
     * Write tabs.
     * @param out file to write
     * @param tabsNumber number of tabs
     * @throws IOException if input-output error occurs
     */
    private static void writeTabs(@NotNull FileWriter out, int tabsNumber)
            throws IOException {
        for (int i = 0; i < tabsNumber; i++) {
            out.write('\t');
        }
    }

    /**
     * Write modifiers of a class/field/method.
     * @param modifier int modifier
     * @param out file to write
     * @param tabsNumber number of tabs
     * @throws IOException if input-output error occurs
     */
    private static void writeModifiers(int modifier,
                                       @NotNull FileWriter out,
                                       int tabsNumber)
            throws IOException {
        writeTabs(out, tabsNumber);
        var stringModifier = Modifier.toString(modifier);
        if (!stringModifier.equals("")) {
            out.write(stringModifier + ' ');
        }
    }

    /**
     * Write parameters of a method/constructor.
     * @param parameters array of parameters
     * @param out file to write
     * @throws IOException if input-output error occurs
     */
    private static void writeParameters(@NotNull Parameter[] parameters,
                                        @NotNull FileWriter out)
            throws IOException {
        if (parameters.length == 0) {
            return;
        }
        out.write(getType(parameters[0].getParameterizedType())
                + " " + parameters[0].getName());
        for (int i = 1; i < parameters.length; i++) {
            out.write(", " + getType(parameters[i].getParameterizedType())
                    + " " + parameters[i].getName());
        }
    }

    /**
     * Write generic type parameters.
     * @param clazz generic type
     * @param out file to write
     * @throws IOException if input-output error occurs
     */
    private static void writeTypeParameters(@NotNull Class<?> clazz,
                                            @NotNull FileWriter out)
            throws IOException {
        var typeParameters = clazz.getTypeParameters();
        if (typeParameters.length > 0) {
            out.write("<" + typeParameters[0].getName());
            for (int i = 1; i < typeParameters.length; i++) {
                out.write(", " + typeParameters[i].getName());
            }
            out.write(">");
        }
    }

    /**
     * Write `extends` section.
     * @param clazz class to find information about
     * @param out file to write
     * @throws IOException if input-output error occurs
     */
    private static void writeExtends(@NotNull Class<?> clazz,
                                     @NotNull FileWriter out)
            throws IOException {
        var parentClass = clazz.getSuperclass();
        var parent = parentClass == null || parentClass == Object.class
                ? null
                : parentClass.getSimpleName();
        if (parent != null) {
            out.write("extends " + parent);
            writeTypeParameters(parentClass, out);
            out.write(" ");
        }
    }

    /**
     * Write `implements` section.
     * @param clazz class to find information about
     * @param out file to write
     * @throws IOException if input-output error occurs
     */
    private static void writeImplements(@NotNull Class<?> clazz,
                                        @NotNull FileWriter out)
            throws IOException {
        var interfaces = clazz.getInterfaces();
        if (interfaces.length != 0) {
            out.write("implements ");
            out.write(interfaces[0].getCanonicalName());
            writeTypeParameters(interfaces[0], out);
            for (int i = 1; i < interfaces.length; i++) {
                out.write(", " + interfaces[i].getCanonicalName());
                writeTypeParameters(interfaces[i], out);
            }
            out.write(" ");
        }
    }

    /**
     * Write difference between two classes.
     * Write all methods and fields that differ.
     * @param a first class
     * @param b second class
     * @param out file to write
     * @throws IOException if input-output error occurs
     */
    public static void diffClasses(@NotNull Class<?> a, @NotNull Class<?> b,
                                   @NotNull FileWriter out)
            throws IOException {
        writeFields(diffFields(a, b), out, -1);
        writeMethods(diffMethods(a, b), out, -1);
    }

    /**
     * Find fields that differ in two classes.
     * @param a first class
     * @param b second class
     * @return array of fields that differ in classes
     */
    @NotNull
    private static Field[] diffFields(@NotNull Class<?> a, @NotNull Class<?> b) {
        var diff = diff(getFieldContainerArray(a), getFieldContainerArray(b));
        var arrayDiff = new Field[diff.size()];
        for (int i = 0; i < diff.size(); i++) {
            arrayDiff[i] = diff.get(i).getField();
        }
        return arrayDiff;
    }

    /**
     * Make and array of FieldContainers that contain fields of a given class.
     * @param clazz class to extract fields from
     * @return FindContainer array
     */
    @NotNull
    private static FieldContainer[] getFieldContainerArray(@NotNull Class<?> clazz) {
        var fields = clazz.getDeclaredFields();
        FieldContainer[] fieldContainers = new FieldContainer[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldContainers[i] = new FieldContainer(fields[i]);
        }
        return fieldContainers;
    }

    /**
     * Find methods that differ in two classes.
     * @param a first class
     * @param b second class
     * @return array of methods that differ
     */
    @NotNull
    private static Method[] diffMethods(@NotNull Class<?> a, @NotNull Class<?> b) {
        var diff = diff(getMethodContainerArray(a), getMethodContainerArray(b));
        var arrayDiff = new Method[diff.size()];
        for (int i = 0; i < diff.size(); i++) {
            arrayDiff[i] = diff.get(i).getMethod();
        }
        return arrayDiff;
    }

    /**
     * Make and array of MethodContainers that contain methods of a given class.
     * @param clazz class to extract methods from
     * @return MethodContainer array
     */
    @NotNull
    private static MethodContainer[] getMethodContainerArray(@NotNull Class<?> clazz) {
        var methods = clazz.getDeclaredMethods();
        MethodContainer[] methodContainers = new MethodContainer[methods.length];
        for (int i = 0; i < methods.length; i++) {
            methodContainers[i] = new MethodContainer(methods[i]);
        }
        return methodContainers;
    }

    /**
     * A class containing a Field.
     * Provides equal function.
     */
    private static class FieldContainer {
        private final Field field;

        private FieldContainer(@NotNull Field field) {
            this.field = field;
        }

        private Field getField() {
            return field;
        }

        /**
         * Find out if field is equal to obj.
         * @param obj object to compare with
         * @return true iff class where field declared,
         * name, type, modifiers are equal for both fields
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FieldContainer)) {
                return false;
            }
            var other = (FieldContainer) obj;
            return field.getName().equals(other.field.getName())
                    && field.getType() == other.field.getType()
                    && Modifier.toString(field.getModifiers()).equals(Modifier.toString(other.field.getModifiers()))
                    && field.getDeclaringClass().getSimpleName().equals(other.field.getDeclaringClass().getSimpleName());
        }
    }

    /**
     * A class containing a Method,
     * Provides equal function.
     */
    private static class MethodContainer {
        private final Method method;

        private MethodContainer(@NotNull Method method) {
            this.method = method;
        }

        private Method getMethod() {
            return method;
        }

        /**
         * Find out if method is equal to obj.
         * @param obj object to compare with
         * @return true iff class where method declared, parameters, exceptions,
         * name, return type, modifiers are equal for both fields
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MethodContainer)) {
                return false;
            }
            var other = (MethodContainer) obj;
            return method.getName().equals(other.method.getName())
                    && method.getReturnType() == other.method.getReturnType()
                    && Modifier.toString(method.getModifiers()).equals(Modifier.toString(other.method.getModifiers()))
                    && method.getDeclaringClass().getSimpleName().equals(other.method.getDeclaringClass().getSimpleName())
                    && Arrays.equals(method.getParameterTypes(), other.method.getParameterTypes())
                    && Arrays.equals(method.getExceptionTypes(), other.method.getExceptionTypes());
        }
    }

    /**
     * Find difference of two arrays.
     * @param a first array
     * @param b second array
     * @param <T> type of elements
     * @return list that contains elements of a not containing in b
     * and elements of b not containing in a
     */
    @NotNull
    private static <T> List<T> diff(@NotNull T[] a, @NotNull T[] b) {
        var bSet = new ArrayList<>(Arrays.asList(b));
        var diff = new ArrayList<T>();
        for (var aElement : a) {
            if (!bSet.contains(aElement)) {
                diff.add(aElement);
            } else {
                bSet.remove(aElement);
            }
        }
        diff.addAll(bSet);
        return diff;
    }
}
