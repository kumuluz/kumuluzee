/**
 * Copyright (c) Sunesis d.o.o.
 */
package com.kumuluz.ee.logs;

import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.logs.messages.LogMessage;

/**
 * Kumuluz-logs logger interface
 *
 * @author Rok Povse
 * @author Marko Skrjanec
 */
public interface Logger {

    Logger getLogger(String logName);

    String getName();

    void log(LogLevel level, String message);

    void log(LogLevel level, String message, Object... args);

    void log(LogLevel level, String message, Throwable thrown);

    void log(LogLevel level, Throwable thrown);

    void log(LogLevel level, LogMessage message);

    void log(LogLevel level, LogMessage message, Throwable thrown);

    void trace(String message);

    void trace(String message, Object... args);

    void trace(String message, Throwable thrown);

    void trace(Throwable thrown);

    void trace(LogMessage message);

    void trace(LogMessage message, Throwable thrown);

    void info(String message);

    void info(String message, Object... args);

    void info(String message, Throwable thrown);

    void info(Throwable thrown);

    void info(LogMessage message);

    void info(LogMessage message, Throwable thrown);

    void debug(String message);

    void debug(String message, Object... args);

    void debug(String message, Throwable thrown);

    void debug(Throwable thrown);

    void debug(LogMessage message);

    void debug(LogMessage message, Throwable thrown);

    void warn(String message);

    void warn(String message, Object... args);

    void warn(String message, Throwable thrown);

    void warn(Throwable thrown);

    void warn(LogMessage message);

    void warn(LogMessage message, Throwable thrown);

    void error(String message);

    void error(String message, Object... args);

    void error(String message, Throwable thrown);

    void error(Throwable thrown);

    void error(LogMessage message);

    void error(LogMessage message, Throwable thrown);
}
