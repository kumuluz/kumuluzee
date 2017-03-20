package com.kumuluz.ee.logs.types;

import com.kumuluz.ee.logs.messages.MethodCallLogMessage;

/**
 * @author Tilen Faganel
 */
public class LogMethodMessage {

    private Boolean callEnabled;
    private Boolean metricsEnabled;

    private MethodCallLogMessage callMessage;

    public LogMethodMessage enableCall(MethodCallLogMessage callMessage) {
        this.callMessage = callMessage;
        this.callEnabled = true;

        return this;
    }

    public LogMethodMessage enableMetrics() {
        this.metricsEnabled = true;

        return this;
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
}
