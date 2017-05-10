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
package com.kumuluz.ee.logs.types;

import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.logs.markers.Marker;
import com.kumuluz.ee.logs.messages.MethodCallExitLogMessage;
import com.kumuluz.ee.logs.messages.MethodCallLogMessage;

/**
 * @author Tilen Faganel
 */
public class LogMethodContext {

    private LogLevel level;
    private Marker marker;

    private Boolean callEnabled;
    private Boolean metricsEnabled;

    private MethodCallLogMessage callMessage;

    private MethodCallExitLogMessage callExitMessage;
    private LogMetrics logMetrics;

    public LogMethodContext(LogMethodMessage entryMessage, LogLevel level, Marker marker) {

        this.callEnabled = entryMessage.isCallEnabled();
        this.metricsEnabled = entryMessage.isMetricsEnabled();

        if (this.metricsEnabled != null && this.metricsEnabled) {
            this.logMetrics = new LogMetrics();
        }

        this.callMessage = entryMessage.getCallMessage();

        this.level = level;

        this.marker = marker;
    }

    public LogMethodContext(LogMethodMessage entryMessage, LogLevel level) {
        this(entryMessage,level,null);
    }

    public Boolean isCallEnabled() {
        return callEnabled;
    }

    public Boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public MethodCallLogMessage getCallMessage() {
        return callMessage;
    }

    public MethodCallExitLogMessage getCallExitMessage() {
        return callExitMessage;
    }

    public void setCallExitMessage(MethodCallExitLogMessage callExitMessage) {
        this.callExitMessage = callExitMessage;
    }

    public LogLevel getLevel() {
        return level;
    }

    public LogMetrics getLogMetrics() {
        return logMetrics;
    }

    public Marker getMarker() {
        return marker;
    }
}
