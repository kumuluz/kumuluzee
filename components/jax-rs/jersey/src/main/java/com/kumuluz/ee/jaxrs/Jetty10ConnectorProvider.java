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
package com.kumuluz.ee.jaxrs;

import org.glassfish.jersey.client.spi.Connector;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.glassfish.jersey.jetty.connector.Jetty10Connector;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;

/**
 * Jersey connection provider for Jetty 10.
 *
 * @author Urban Malc
 * @since 4.0.0
 */
public class Jetty10ConnectorProvider implements ConnectorProvider {

    @Override
    public Connector getConnector(Client client, Configuration runtimeConfig) {
        return new Jetty10Connector(client, runtimeConfig);
    }
}
