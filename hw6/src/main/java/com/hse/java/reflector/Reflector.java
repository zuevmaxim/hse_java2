package com.hse.java.reflector;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

public class Reflector {

    private static void writeTabs(FileWriter out, int tabsNumber) throws IOException {
        for (int i = 0; i < tabsNumber; i++) {
            out.write('\t');
        }
    }

    private static void writeModifiers(int modifier, FileWriter out, int tabsNumber) throws IOException {
        writeTabs(out, tabsNumber);
        if (Modifier.isPrivate(modifier)) {
            out.write("private ");
        } else if (Modifier.isProtected(modifier)) {
            out.write("protected ");
        } else if (Modifier.isPublic(modifier)) {
            out.write("public ");
        }
        if (Modifier.isAbstract(modifier)) {
            out.write("abstract ");
        }
        if (Modifier.isStatic(modifier)) {
            out.write("static ");
        }
        if (Modifier.isFinal(modifier)) {
            out.write("final ");
        }
    }

    private static void writeParameters(Parameter[] parameters, FileWriter out) throws IOException {
        if (parameters.length == 0) {
            return;
        }
        out.write(parameters[0].getParameterizedType().getTypeName() + " " + parameters[0].getName());
        for (int i = 1; i < parameters.length; i++) {
            out.write(", " + parameters[i].getParameterizedType().getTypeName() + " " + parameters[i].getName());
        }
    }

    private static void writeTypeParameters(Class<?> clazz, FileWriter out) throws IOException {
        var typeParameters = clazz.getTypeParameters();
        if (typeParameters.length > 0) {
            out.write("<" + typeParameters[0].getName());
            for (int i = 1; i < typeParameters.length; i++) {
                out.write(", " + typeParameters[i].getName());
            }
            out.write(">");
        }
    }

    public static void  printStructure(Class<?> someClass) throws IOException {
        try (var out = new FileWriter(someClass.getSimpleName() + ".java")) {
            recursivePrintStructure(someClass, out, 0);
        }
    }

    private static void writeExtends(Class<?> clazz, FileWriter out) throws IOException {
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

    private static void writeImplements(Class<?> clazz, FileWriter out) throws IOException {
        var interfaces = clazz.getInterfaces();
        if (interfaces.length != 0) {
            out.write("implements ");
            out.write(interfaces[0].getSimpleName());
            writeTypeParameters(interfaces[0], out);
            for (int i = 1; i < interfaces.length; i++) {
                out.write(", " + interfaces[i].getSimpleName());
                writeTypeParameters(interfaces[i], out);
            }
            out.write(" ");
        }
    }

    private static void recursivePrintStructure(
            Class<?> someClass,
            FileWriter out,
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

        for (var field : someClass.getDeclaredFields()) {
            writeModifiers(field.getModifiers(), out, tabsNumber + 1);
            out.write(field.getGenericType().getTypeName());
            out.write(" " + field.getName() + ";\n");
        }
        out.write("\n");

        for (var constructor : someClass.getDeclaredConstructors()) {
            writeModifiers(constructor.getModifiers(), out, tabsNumber + 1);
            out.write(name + "(");
            writeParameters(constructor.getParameters(), out);
            out.write(");\n");
        }
        out.write("\n");

        for (var method : someClass.getDeclaredMethods()) {
            writeModifiers(method.getModifiers(), out, tabsNumber + 1);
            out.write(method.getGenericReturnType().getTypeName() + " ");
            out.write(method.getName() + "(");
            writeParameters(method.getParameters(), out);
            out.write(");\n");
        }

        writeTabs(out, tabsNumber);
        out.write("}\n\n");

    }

    public static void diffClasses(Class<?> a, Class<?> b) {

    }

    public static void main(String[] args) throws IOException {
        printStructure(String.class);
        printStructure(ArrayList.class);
    }
}
