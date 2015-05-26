package com.kumuluz.ee.loaders;

import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * @author Tilen
 */
public class ServerLoader {

    Logger log = Logger.getLogger(ServerLoader.class.getSimpleName());

    public ServletServer loadServletServer() {

        log.info("Loading the http/servlet server...");

        List<ServletServer> serversClasses = scanForAvailableServers();

        if (serversClasses.isEmpty()) {

            String msg = "No supported servers were found. Please add one of them to the class " +
                    "path. For example to add Jetty add the 'kumuluzee-servlet-jetty' module as a" +
                    " dependency. For additional servers refer to the documentation";

            log.severe(msg);

            throw new KumuluzServerException(msg);
        }

        if (serversClasses.size() > 1) {

            String msg = "Multiple servers were found. Only one can be used at a time, please " +
                    "remove all but one servers. For additional information refer to the " +
                    "documentation";

            log.severe(msg);

            throw new KumuluzServerException(msg);
        }

        ServletServer server = serversClasses.get(0);

        log.info("Found " + server.getClass().getSimpleName());

        return server;
    }

    private List<ServletServer> scanForAvailableServers() {

        log.finest("Scanning for available supported http/servlet servers");

        List<ServletServer> servers = new ArrayList<>();

        ServiceLoader.load(ServletServer.class).forEach(servers::add);

        return servers;
    }
}
