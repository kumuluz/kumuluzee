package com.kumuluz.ee.common.utils;

import java.util.List;

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

    public static List<String> getClassNamesWithAnnotation(Class<?> annotation) {

        return ClassScanner.getInstance().getClassesWithAnnotation(annotation);
    }
}
