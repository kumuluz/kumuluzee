package com.kumuluz.ee.common.utils;

/**
 * @author Tilen Faganel
 * @since 1.0.0
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
}
