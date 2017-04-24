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
