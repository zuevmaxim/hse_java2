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


public class ReflectorTest {

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

}