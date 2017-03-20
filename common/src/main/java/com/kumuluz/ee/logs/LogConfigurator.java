package com.kumuluz.ee.logs;

import com.kumuluz.ee.logs.enums.LogLevel;

/**
 * @author Rok Povse
 * @author Marko Skrjanec
 */
public interface LogConfigurator {

    void init();

    void setLevel(String logName, LogLevel logLevel);

    LogLevel getLevel(String logName);

}
