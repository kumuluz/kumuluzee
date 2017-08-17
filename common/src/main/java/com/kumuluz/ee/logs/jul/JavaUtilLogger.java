/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
*/

package com.kumuluz.ee.logs.jul;

import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.logs.jul.utils.JavaUtilLogUtil;
import com.kumuluz.ee.logs.messages.LogMessage;

/**
 * @author Marko Skrjanec
 */
public class JavaUtilLogger implements Logger {

    private java.util.logging.Logger logger;

    public JavaUtilLogger() {
    }

    private JavaUtilLogger(String logName) {
        logger = java.util.logging.Logger.getLogger(logName);
    }

    @Override
    public Logger getLogger(String logName) {
        JavaUtilLogger julLogger = new JavaUtilLogger(logName);
        return julLogger;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public void log(LogLevel level, String message) {
        logger.log(JavaUtilLogUtil.convertToJULLevel(level), message);
    }

    @Override
    public void log(LogLevel level, String message, Object... args) {
        logger.log(JavaUtilLogUtil.convertToJULLevel(level), message, args);
    }

    @Override
    public void log(LogLevel level, String message, Throwable thrown) {
        logger.log(JavaUtilLogUtil.convertToJULLevel(level), message, thrown);
    }

    @Override
    public void log(LogLevel level, Throwable thrown) {
        logger.log(JavaUtilLogUtil.convertToJULLevel(level), thrown.getMessage(), thrown.getCause());
    }

    @Override
    public void log(LogLevel level, LogMessage message) {
        log(level, message, null);
    }

    @Override
    public void log(LogLevel level, LogMessage message, Throwable thrown) {
        if (message == null) {
            logger.log(JavaUtilLogUtil.convertToJULLevel(level), message.toString(), thrown);
        } else if (message.getFields() == null) {
            logger.log(JavaUtilLogUtil.convertToJULLevel(level), message.getMessage(), thrown);
        } else {
            logger.log(JavaUtilLogUtil.convertToJULLevel(level),
                    message.getMessage() + " " + message.getFields().toString(), thrown);
        }
    }

    @Override
    public void trace(String message) {
        log(LogLevel.TRACE, message);
    }

    @Override
    public void trace(String message, Object... args) {
        log(LogLevel.TRACE, message, args);
    }

    @Override
    public void trace(String message, Throwable thrown) {
        log(LogLevel.TRACE, message, thrown);
    }

    @Override
    public void trace(Throwable thrown) {
        log(LogLevel.TRACE, thrown);
    }

    @Override
    public void trace(LogMessage message) {
        log(LogLevel.TRACE, message);
    }

    @Override
    public void trace(LogMessage message, Throwable thrown) {
        log(LogLevel.TRACE, message, thrown);
    }

    @Override
    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    @Override
    public void info(String message, Object... args) {
        log(LogLevel.INFO, message, args);
    }

    @Override
    public void info(String message, Throwable thrown) {
        log(LogLevel.INFO, message, thrown);
    }

    @Override
    public void info(Throwable thrown) {
        log(LogLevel.INFO, thrown);
    }

    @Override
    public void info(LogMessage message) {
        log(LogLevel.INFO, message);
    }

    @Override
    public void info(LogMessage message, Throwable thrown) {
        log(LogLevel.INFO, message, thrown);
    }

    @Override
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    @Override
    public void debug(String message, Object... args) {
        log(LogLevel.DEBUG, message, args);
    }

    @Override
    public void debug(String message, Throwable thrown) {
        log(LogLevel.DEBUG, message, thrown);
    }

    @Override
    public void debug(Throwable thrown) {
        log(LogLevel.DEBUG, thrown);
    }

    @Override
    public void debug(LogMessage message) {
        log(LogLevel.DEBUG, message);
    }

    @Override
    public void debug(LogMessage message, Throwable thrown) {
        log(LogLevel.DEBUG, message, thrown);
    }

    @Override
    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    @Override
    public void warn(String message, Object... args) {
        log(LogLevel.WARN, message, args);
    }

    @Override
    public void warn(String message, Throwable thrown) {
        log(LogLevel.WARN, message, thrown);
    }

    @Override
    public void warn(Throwable thrown) {
        log(LogLevel.WARN, thrown);
    }

    @Override
    public void warn(LogMessage message) {
        log(LogLevel.WARN, message);
    }

    @Override
    public void warn(LogMessage message, Throwable thrown) {
        log(LogLevel.WARN, message, thrown);
    }

    @Override
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    @Override
    public void error(String message, Object... args) {
        log(LogLevel.ERROR, message, args);
    }

    @Override
    public void error(String message, Throwable thrown) {
        log(LogLevel.ERROR, message, thrown);
    }

    @Override
    public void error(Throwable thrown) {
        log(LogLevel.ERROR, thrown);
    }

    @Override
    public void error(LogMessage message) {
        log(LogLevel.ERROR, message);
    }

    @Override
    public void error(LogMessage message, Throwable thrown) {
        log(LogLevel.ERROR, message, thrown);
    }

}
