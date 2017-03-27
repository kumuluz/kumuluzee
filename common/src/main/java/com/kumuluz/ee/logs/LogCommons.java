/**
 * Copyright (c) Sunesis d.o.o.
 */
package com.kumuluz.ee.logs;

import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.logs.markers.Marker;
import com.kumuluz.ee.logs.types.LogMethodContext;
import com.kumuluz.ee.logs.types.LogMethodMessage;
import com.kumuluz.ee.logs.types.LogResourceContext;
import com.kumuluz.ee.logs.types.LogResourceMessage;

/**
 * Kumuluz-logs logger interface
 *
 * @author Rok Povse
 * @author Marko Skrjanec
 */
public interface LogCommons {

    /**
     * Creates a new logger
     *
     * @param logName String name of the logger
     * @return Logger instance
     */
    LogCommons getCommonsLogger(String logName);

    /**
     * Returns logger name
     *
     * @return String logger name
     */
    String getName();

    /**
     * Set the level for logging. Default is TRACE
     *
     * @param logLevel LogLevel object defining LogCommons
     */
    void setDefaultLevel(LogLevel logLevel);

    /**
     * Log Method entry
     *
     * @param logMethodMessage LogMethodMessage object with details about log content
     * @return LogMethodContext object, which is passed to logMethodExit
     */
    LogMethodContext logMethodEntry(LogMethodMessage logMethodMessage);

    /**
     * Log Method entry with custom LogLevel definition
     *
     * @param level            LogLevel
     * @param logMethodMessage LogMethodMessage object with details about log content
     * @return LogMethodContext object, which is passed to logMethodExit
     */
    LogMethodContext logMethodEntry(LogLevel level, LogMethodMessage logMethodMessage);

    /**
     * Log Method entry with custom LogLevel definition
     *
     * @param marker             Marker object defining type of resource
     * @param logMethodMessage LogMethodMessage object with details about log content
     * @return LogMethodContext object, which is passed to logMethodExit
     */
    LogMethodContext logMethodEntry(Marker marker, LogMethodMessage logMethodMessage);

    /**
     * Log Method entry with custom LogLevel definition
     *
     * @param level            LogLevel
     * @param marker             Marker object defining type of resource
     * @param logMethodMessage LogMethodMessage object with details about log content
     * @return LogMethodContext object, which is passed to logMethodExit
     */
    LogMethodContext logMethodEntry(LogLevel level, Marker marker, LogMethodMessage logMethodMessage);

    /**
     * Log Method exit
     *
     * @param logMethodContext LogMethodContext object with details returned from Entry log
     */
    void logMethodExit(LogMethodContext logMethodContext);

    /**
     * Log Resource invokation start
     *
     * @param logResourceMessage LogResourceMessage object with details about invoked resource and configuration
     * @return LogResourceContext object, which is passed to logResourceEnd
     */
    LogResourceContext logResourceStart(LogResourceMessage logResourceMessage);

    /**
     * Log Resource invokation start
     *
     * @param level              LogLevel
     * @param logResourceMessage LogResourceMessage object with details about invoked resource and configuration
     * @return LogResourceContext object, which is passed to logResourceEnd
     */
    LogResourceContext logResourceStart(LogLevel level, LogResourceMessage logResourceMessage);


    /**
     * Log Resource invokation start
     *
     * @param marker             Marker object defining type of resource
     * @param logResourceMessage LogResourceMessage object with details about invoked resource and configuration
     * @return LogResourceContext object, which is passed to logResourceEnd
     */
    LogResourceContext logResourceStart(Marker marker, LogResourceMessage logResourceMessage);

    /**
     * Log Resource invokation start
     *
     * @param level              LogLevel
     * @param marker             Marker object defining type of resource
     * @param logResourceMessage LogResourceMessage object with details about invoked resource and configuration
     * @return LogResourceContext object, which is passed to logResourceEnd
     */
    LogResourceContext logResourceStart(LogLevel level, Marker marker, LogResourceMessage logResourceMessage);

    /**
     * Log end of Resource invoke
     *
     * @param logResourceContext LogResourceContext object with details returned from logResourceStart method
     */
    void logResourceEnd(LogResourceContext logResourceContext);

}
