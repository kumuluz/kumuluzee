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

package com.kumuluz.ee.logs.jul.utils;

import com.kumuluz.ee.logs.enums.LogLevel;

import java.util.logging.Level;

/**
 * @author Marko Skrjanec
 */
public class JULLogUtil {

    public static final String JUL_LOGGER_NAME = "JULLogger";

    public static Level convertToJULLevel(LogLevel logLevel) {
        switch (logLevel) {
            case ERROR:
                return Level.SEVERE;
            case WARN:
                return Level.WARNING;
            case INFO:
                return Level.INFO;
            case DEBUG:
                return Level.FINE;
            case TRACE:
                return Level.FINER;
            default:
                return null;
        }
    }

    public static LogLevel convertFromJULLevel(Level level) {
        if (Level.SEVERE.equals(level)) {
            return LogLevel.ERROR;
        } else if (Level.WARNING.equals(level)) {
            return LogLevel.WARN;
        } else if (Level.INFO.equals(level)) {
            return LogLevel.INFO;
        } else if (Level.FINE.equals(level)) {
            return LogLevel.DEBUG;
        } else if (Level.FINER.equals(level)) {
            return LogLevel.TRACE;
        } else {
            return null;
        }
    }
}
