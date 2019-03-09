package com.hse.java.reflector;

import com.hse.java.reflector.testClasses.*;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.jupiter.api.Assertions.*;


class ReflectorTest {

    @Test
    void testDiff() throws IOException, ClassNotFoundException {
        Reflector.printStructure(FirstClassTestDiff.class);
        Class<?> first = compileAndLoad(FirstClassTestDiff.class.getSimpleName());
        Reflector.printStructure(SecondClassTestDiff.class);
        Class<?> second = compileAndLoad(SecondClassTestDiff.class.getSimpleName());
        String diffFileName = "diff.txt";
        File diffFile = new File(diffFileName);
        try (var out = new FileWriter(diffFile)) {
            Reflector.diffClasses(first, second, out);
        }
        String result;
        try (var in = new FileReader(diffFile)) {
            var data = new char[(int) diffFile.length()];
            //noinspection ResultOfMethodCallIgnored
            in.read(data);
            result = new String(data);
        }
        assertEquals(result, "int x;\n"
                + "int x;\n"
                + "void foo() { }\n"
                + "void boo() { }\n");
    }

    private void test(Class<?> clazz) throws IOException, ClassNotFoundException {
        Reflector.printStructure(clazz);
        Class<?> other = compileAndLoad(clazz.getSimpleName());
        String diffFileName = "diff.txt";
        try (var out = new FileWriter(diffFileName)) {
            Reflector.diffClasses(clazz, other, out);
        }
        try (var in = new FileReader(diffFileName)) {
            assertEquals(-1, in.read());
        }
    }

    private Class<?> compileAndLoad(String className) throws ClassNotFoundException, MalformedURLException {
        File sourcePath = new File(className + ".java");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourcePath.getAbsolutePath());

        URL classUrl = new File(".").toURI().toURL();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{ classUrl });
        return Class.forName(className, true, classLoader);
    }

    @Test
    void testPrimitiveFields() throws IOException, ClassNotFoundException {
        test(ClassPrimitiveFields.class);
    }

    @Test
    void testObjectFields() throws IOException, ClassNotFoundException {
        test(ClassObjectFields.class);
    }

    @Test
    void testModifiersFields() throws IOException, ClassNotFoundException {
        test(ClassModifiersFields.class);
    }

    @Test
    void testGeneric() throws IOException, ClassNotFoundException {
        test(ClassGeneric.class);
    }

    @Test
    void testModifiersMethods() throws IOException, ClassNotFoundException {
        test(ClassModifiersMethods.class);
    }

    @Test
    void testExceptions() throws IOException, ClassNotFoundException {
        test(ClassWithExceptions.class);
    }

    @Test
    void testSubClasses() throws IOException, ClassNotFoundException {
        test(ClassWithSubclasses.class);
    }

    @Test
    void testExtendsClass() throws IOException, ClassNotFoundException {
        test(ClassExtends.class);
    }

    @Test
    void testConstructors() throws IOException, ClassNotFoundException {
        test(ClassWithConstructors.class);
    }

    @Test
    void testMethodOverload() throws IOException, ClassNotFoundException {
        test(ClassWithMethodOverload.class);
    }

    @Test
    void testImplements() throws IOException, ClassNotFoundException {
        test(ClassImplements.class);
    }
}