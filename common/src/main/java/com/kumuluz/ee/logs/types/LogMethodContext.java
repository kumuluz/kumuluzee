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
