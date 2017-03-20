/**
 * Copyright (c) Sunesis d.o.o.
 */
package com.kumuluz.ee.logs;

import com.kumuluz.ee.logs.enums.LogLevel;

/**
 * @author Rok Povse
 * @author Marko Skrjanec
 */
public class LogManager {

    /**
     * Returns Logger instance. Use this method for retrieval of Logger instances in your code.
     *
     * @param loggerName String logger name
     * @return Logger instance
     */
    public static Logger getLogger(String loggerName) {
        return LogUtil.getInstance().getLogInstance(loggerName);
    }

    /**
     * Returns LogCommons instance. Use this method for retrieval of LogCommons instances in your code.
     *
     * @param loggerName String logger name
     * @return LogCommons instance
     */
    public static LogCommons getCommonsLogger(String loggerName) {
        return LogUtil.getInstance().getLogCommonsInstance(loggerName);
    }

    /**
     * Method for getting currently set logger level from code
     *
     * @param loggerName String logger name
     * @return LogLevel object
     */
    public static LogLevel getLogLevel(String loggerName) {
        return LogUtil.getInstance().getLogConfigurator().getLevel(loggerName);
    }

    /**
     * Method for changing logger level from code
     *
     * @param loggerName String logger name
     * @param logLevel   LogLevel object
     */
    public static void setLogLevel(String loggerName, LogLevel logLevel) {
        LogUtil.getInstance().getLogConfigurator().setLevel(loggerName, logLevel);
    }
}
