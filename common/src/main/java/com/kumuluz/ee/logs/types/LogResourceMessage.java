/**
 * Copyright (c) Sunesis d.o.o.
 */
package com.kumuluz.ee.logs.types;


import com.kumuluz.ee.logs.messages.ResourceInvokeLogMessage;

/**
 * @author Tilen Faganel
 */
public class LogResourceMessage {

    private Boolean invokeEnabled;
    private Boolean metricsEnabled;

    private ResourceInvokeLogMessage invokeMessage;

    public LogResourceMessage enableInvoke(ResourceInvokeLogMessage invokeMessage) {
        this.invokeMessage = invokeMessage;
        this.invokeEnabled = true;

        return this;
    }

    public LogResourceMessage enableMetrics() {
        this.metricsEnabled = true;

        return this;
    }

    public Boolean isInvokeEnabled() {
        return invokeEnabled;
    }

    public Boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public ResourceInvokeLogMessage getInvokeMessage() {
        return invokeMessage;
    }
}
