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
