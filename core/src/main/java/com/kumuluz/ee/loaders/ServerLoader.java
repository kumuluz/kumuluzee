package com.kumuluz.ee.loaders;

import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.exceptions.ServletServerException;
import com.kumuluz.ee.common.utils.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * @author Tilen
 */
public class ServerLoader {

    Logger log = Logger.getLogger(ServerLoader.class.getSimpleName());

    private static final String[] availableServers = {
            "com.kumuluz.ee.jetty.JettyServletServer"
    };

    public ServletServer loadServletServer() {

        log.info("Loading the http/servlet server...");

        ArrayList<Class<?>> serversClasses = scanForAvailableServers();

        if (serversClasses.isEmpty()) {

            String msg = "No supported servers were found. Please add one of them to the class " +
                    "path. For example to add Jetty add the 'kumuluzee-servlet-jetty' module as a" +
                    " dependency. For additional servers refer to the documentation";

            log.severe(msg);

            throw new ServletServerException(msg);
        }

        if (serversClasses.size() > 1) {

            String msg = "Multiple servers were found. Only one can be used at a time, please " +
                    "remove all but one servers. For additional information refer to the " +
                    "documentation";

            log.severe(msg);

            throw new ServletServerException(msg);
        }

        log.info("Found " + serversClasses.get(0).getSimpleName());

        ServletServer server;

        try {
            server = (ServletServer) serversClasses.get(0).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {

            log.severe("Failed to instantiate: " + serversClasses.get(0).getSimpleName());

            throw new IllegalStateException(e.getMessage(), e.getCause());
        }

        return server;
    }

    private ArrayList<Class<?>> scanForAvailableServers() {

        log.finest("Scanning for available supported http/servlet servers");

        ArrayList<Class<?>> servers = new ArrayList<>();

        Arrays.stream(availableServers)
                .filter(ClassUtils::isPresent)
                .forEach(c -> servers.add(ClassUtils.loadClass(c)));

        return servers;
    }
}
