package com.kumuluz.ee.logs.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.*;

public class JavaUtilDefaultLogConfigurator {

    public static void init() {

        String javaUtilConfig = System.getProperty("java.util.logging.config.file");

        if (javaUtilConfig == null) {

            LogManager.getLogManager().reset();

            Logger rootLogger = LogManager.getLogManager().getLogger("");

            JavaUtilConsoleHandler handler = new JavaUtilConsoleHandler();

            rootLogger.addHandler(handler);
        } else {

            InputStream javaUtilConfigStream = JavaUtilDefaultLogConfigurator.class.getClassLoader()
                    .getResourceAsStream(javaUtilConfig);

            if (javaUtilConfigStream != null) {

                try {
                    LogManager.getLogManager().readConfiguration(javaUtilConfigStream);
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static void initSoleHandler(Handler handler) {

        LogManager.getLogManager().reset();

        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.addHandler(handler);
    }
}