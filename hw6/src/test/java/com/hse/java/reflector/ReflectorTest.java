package com.hse.java.reflector;

import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectorTest {
    private final String diffFileName = "diff.txt";

    class MyClass<T, K> {
        private final T t;
        private final K k;

        MyClass(T t, K k) {
            this.k = k;
            this.t = t;
        }

        T getT() {
            return t;
        }
    }

    @Test
    void firstTest() throws IOException, ClassNotFoundException {
        Reflector.printStructure(MyClass.class);

        File sourcePath = new File(MyClass.class.getSimpleName() + ".java");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, System.out, System.err, sourcePath.getAbsolutePath());

        URL classUrl = new File(".").toURI().toURL();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{ classUrl });
        Class<?> clazz = Class.forName(MyClass.class.getSimpleName(), true, classLoader);

        Class<?> other = clazz;
        try (var out = new FileWriter(diffFileName)) {
            Reflector.diffClasses(MyClass.class, other, out);
        }
        try (var in = new FileReader(diffFileName)) {
            assertEquals(-1, in.read());
        }
    }

}