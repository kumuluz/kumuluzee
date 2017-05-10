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
/**
 * Copyright (c) Sunesis d.o.o.
 */
package com.kumuluz.ee.logs.types;

import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.logs.markers.Marker;
import com.kumuluz.ee.logs.messages.ResourceInvokeEndLogMessage;
import com.kumuluz.ee.logs.messages.ResourceInvokeLogMessage;

/**
 * @author Tilen Faganel
 */
public class LogResourceContext {

    private LogLevel level;
    private Marker marker;

    private Boolean invokeEnabled;
    private Boolean metricsEnabled;

    private ResourceInvokeLogMessage invokeMessage;

    private ResourceInvokeEndLogMessage invokeEndMessage;
    private LogMetrics logMetrics;

    public LogResourceContext(LogResourceMessage resourceMessage, LogLevel level, Marker marker) {
        this.invokeEnabled = resourceMessage.isInvokeEnabled();
        this.metricsEnabled = resourceMessage.isMetricsEnabled();

        if (this.metricsEnabled != null && this.metricsEnabled) {
            this.logMetrics = new LogMetrics();
        }

        this.invokeMessage = resourceMessage.getInvokeMessage();

        this.level = level;
        this.marker = marker;
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

    public ResourceInvokeEndLogMessage getInvokeEndMessage() {
        return invokeEndMessage;
    }

    public void setInvokeEndMessage(ResourceInvokeEndLogMessage invokeEndMessage) {
        this.invokeEndMessage = invokeEndMessage;
    }

    public LogLevel getLevel() {
        return level;
    }

    public Marker getMarker() {
        return marker;
    }

    public LogMetrics getLogMetrics() {
        return logMetrics;
    }
}
