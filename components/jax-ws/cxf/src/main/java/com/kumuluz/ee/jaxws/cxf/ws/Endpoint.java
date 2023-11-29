/*
 *  Copyright (c) 2014-2018 Kumuluz and/or its affiliates
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
package com.kumuluz.ee.jaxws.cxf.ws;

import jakarta.jws.WebService;

/**
 * @author gpor89
 * @since 3.0.0
 */
public class Endpoint {

    private String url;
    private Class<?> implementationClass;
    private WebService ws;

    public Endpoint(Class<?> implementationClass, String url, WebService webService) {
        this.url = url;
        this.implementationClass = implementationClass;
        this.ws = webService;
    }

    public String getUrl() {
        return url != null ? url : defaultEndpointUrl();
    }

    public Class<?> getImplementationClass() {
        return implementationClass;
    }

    public String wsdlLocation() {

        if (ws == null) {
            return null;
        }

        return ws.wsdlLocation() != null && ws.wsdlLocation().isEmpty() ? null : ws.wsdlLocation();
    }

    private String defaultEndpointUrl() {

        if (ws == null || implementationClass == null) {
            return null;
        }

        return "/" + implementationClass.getName();
    }

    public String serviceName() {

        if (ws == null) {
            return null;
        }

        return ws.serviceName() != null && ws.serviceName().isEmpty() ? null : ws.serviceName();
    }

    public String portName() {

        if (ws == null) {
            return null;
        }

        return ws.portName() != null && ws.portName().isEmpty() ? null : ws.portName();
    }
}
