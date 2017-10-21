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
package com.kumuluz.ee.loaders;

import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.dependencies.ServerDef;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class ServerLoader {

    private static Logger log = Logger.getLogger(ServerLoader.class.getSimpleName());

    public static KumuluzServer loadServletServer() {

        log.info("Loading the KumuluzEE server...");

        List<KumuluzServer> serversClasses = scanForAvailableServers();

        if (serversClasses.isEmpty()) {

            String msg = "No supported servers were found. Please add one of them to the class " +
                    "path. For example to add Jetty add the 'kumuluzee-servlet-jetty' module as a" +
                    " dependency. For additional servers refer to the documentation.";

            log.severe(msg);

            throw new KumuluzServerException(msg);
        }

        if (serversClasses.size() > 1) {

            String msg = "Multiple servers were found. Only one can be used at a time, please " +
                    "remove all but one servers. For additional information refer to the " +
                    "documentation.";

            log.severe(msg);

            throw new KumuluzServerException(msg);
        }

        KumuluzServer server = serversClasses.get(0);

        ServerDef serverDef = server.getClass().getDeclaredAnnotation(ServerDef.class);

        if (serverDef == null) {

            String msg = "The found class \"" + server.getClass().getSimpleName()  + "\" is missing the @ServerDef" +
                    "annotation. The annotation is required in order to correctly process the specific " +
                    "implementation and its components.";

            log.severe(msg);

            throw new KumuluzServerException(msg);
        }

        log.info("Found " + serverDef.value());

        return server;
    }

    private static List<KumuluzServer> scanForAvailableServers() {

        log.finest("Scanning for available supported KumuluzEE servers");

        List<KumuluzServer> servers = new ArrayList<>();

        ServiceLoader.load(KumuluzServer.class).forEach(servers::add);

        return servers;
    }
}
