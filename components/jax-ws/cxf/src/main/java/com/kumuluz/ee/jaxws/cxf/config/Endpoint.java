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
package com.kumuluz.ee.jaxws.cxf.config;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author gpor89
 * @since 2.6.0
 */
public class Endpoint {

    protected static final String JAX_WS_CONFIG_PATH = "kumuluzee.jax-ws";

    private static Logger LOG = Logger.getLogger(Endpoint.class.getSimpleName());


    private Optional<String> path;
    private Optional<String> implementationClass;

    private Endpoint(Optional<String> path, Optional<String> implementationClass) {
        this.path = path;
        this.implementationClass = implementationClass;
    }

    public static String readWebContextPath(ConfigurationUtil cfg) {
        final Set<String> contextPathSet = new HashSet<>();

        Optional<Integer> jaxWsConfigLength = cfg.getListSize(JAX_WS_CONFIG_PATH);

        for (int i = 0; jaxWsConfigLength.isPresent() && i < jaxWsConfigLength.get(); i++) {

            Optional<String> webCtxConfig = cfg.get(JAX_WS_CONFIG_PATH + "[" + i + "].web-context");

            if (!webCtxConfig.isPresent()) {
                LOG.warning("Invalid jax-ws endpoint web-context = " + webCtxConfig);
                continue;
            }

            contextPathSet.add(webCtxConfig.get());
        }

        if (contextPathSet.size() > 0) {
            LOG.warning("Webservice component does not support multiple web-contexts");
        }

        return contextPathSet.isEmpty() ? null : contextPathSet.stream().findFirst().get();
    }

    public static List<Endpoint> readEndpointList(ConfigurationUtil cfg, String contextRoot) {

        final List<Endpoint> endpointList = new LinkedList<>();

        Optional<Integer> jaxWsConfigLength = cfg.getListSize(JAX_WS_CONFIG_PATH);

        for (int i = 0; jaxWsConfigLength.isPresent() && i < jaxWsConfigLength.get(); i++) {

            Optional<String> webCtxConfig = cfg.get(JAX_WS_CONFIG_PATH + "[" + i + "].web-context");

            if (!webCtxConfig.isPresent() || !webCtxConfig.get().equalsIgnoreCase(contextRoot)) {
                LOG.warning("Invalid jax-ws endpoint web-context = " + webCtxConfig);
                continue;
            }

            Optional<String> path = cfg.get(JAX_WS_CONFIG_PATH + "[" + i + "].url");
            Optional<String> implementationClass = cfg.get(JAX_WS_CONFIG_PATH + "[" + i + "].implementation-class");

            if (!path.isPresent() || !implementationClass.isPresent()) {

                String invalidConfigItem = cfg.get(JAX_WS_CONFIG_PATH + "[" + i + "]").orElse("");

                LOG.warning("Invalid jax-ws endpoint configuration = " + invalidConfigItem);
                continue;
            }

            endpointList.add(new Endpoint(path, implementationClass));
        }

        return endpointList;
    }

    public String getPath() {
        return path.orElse(null);
    }

    public Class<?> getImplementationClass() {

        if (implementationClass.isPresent()) {
            try {
                return Class.forName(implementationClass.get());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Invalid webservice implementation class", e);
            }
        }

        throw new RuntimeException("Webservice implementation class not found");
    }

}
