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
package com.kumuluz.ee.jaxws.cxf;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.jaxws.cxf.ws.CXFWebservicePublisher;
import com.kumuluz.ee.jaxws.cxf.ws.Endpoint;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author gpor89
 * @since 3.0.0
 */
public class KumuluzCXFServlet extends CXFNonSpringServlet {

    protected static final String CDI_INIT_PARAM = "useCdi";
    protected static final String CONTEXT_ROOT = "contextRoot";
    protected static final String JAX_WS_ENDPOINTS_PATH = "kumuluzee.jax-ws.endpoints";

    private static Logger LOG = Logger.getLogger(KumuluzCXFServlet.class.getSimpleName());

    private List<Endpoint> endpoints;

    public void init() throws ServletException {
        super.init();

        this.endpoints = readEndpointList();
    }

    @Override
    protected void loadBus(ServletConfig sc) {
        super.loadBus(sc);

        boolean cdiPresent = "true".equalsIgnoreCase(getInitParameter(CDI_INIT_PARAM));

        final CXFWebservicePublisher publisher = new CXFWebservicePublisher();

        endpoints.stream().forEach(e -> publisher.publish(e, bus, cdiPresent));

        publisher.close();
    }

    protected final List<Endpoint> readEndpointList() {

        final ConfigurationUtil conf = ConfigurationUtil.getInstance();

        final List<Endpoint> endpointList = new LinkedList<>();

        Optional<Integer> jaxWsConfigLength = conf.getListSize(JAX_WS_ENDPOINTS_PATH);

        for (int i = 0; jaxWsConfigLength.isPresent() && i < jaxWsConfigLength.get(); i++) {

            Optional<String> url = conf.get(JAX_WS_ENDPOINTS_PATH + "[" + i + "].url");
            Optional<String> implementationClass = conf.get(JAX_WS_ENDPOINTS_PATH + "[" + i + "].implementation-class");

            if (!url.isPresent() || !implementationClass.isPresent()) {
                LOG.warning("Invalid jax-ws endpoint configuration = " + conf.get(JAX_WS_ENDPOINTS_PATH + "[" + i + "]").orElse(""));
                continue;
            }

            endpointList.add(new Endpoint(url, implementationClass));
        }

        return endpointList;
    }

}
