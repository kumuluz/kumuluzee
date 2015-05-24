package com.kumuluz.ee.common.utils;

import java.util.ArrayList;
import java.util.List;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

/**
 * @author Tilen
 */
public class ClassUtils {

    public static boolean isPresent(String className) {

        return loadClass(className) != null;
    }

    public static Class<?> loadClass(String className) {

        try {
            return Class.forName(className);
        } catch (Throwable ex) {
            return null;
        }
    }

    public static List<Class<?>> getClassesWithAnnotation(Class<?> annotation) {

        ArrayList<Class<?>> classes = new ArrayList<>();

        new FastClasspathScanner("kumuluzee")
                .matchClassesWithAnnotation(annotation, classes::add)
                .scan();

        return classes;
    }
}
