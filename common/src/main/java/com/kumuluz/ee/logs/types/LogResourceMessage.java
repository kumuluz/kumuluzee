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
