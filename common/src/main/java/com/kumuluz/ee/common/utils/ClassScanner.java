package com.kumuluz.ee.common.utils;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

/**
 * @author Tilen
 */
public class ClassScanner {

    private static ClassScanner instance = new ClassScanner();

    private FastClasspathScanner scanner;

    public ClassScanner() {

        scanner = new FastClasspathScanner("kumuluzee");
        scanner.scan();
    }

    public static FastClasspathScanner getInstance() {

        return instance.getScanner();
    }

    public FastClasspathScanner getScanner() {

        return scanner;
    }
}
