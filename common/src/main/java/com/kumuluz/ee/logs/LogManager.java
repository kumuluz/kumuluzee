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
