package com.kumuluz.ee.jetty;

import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.exceptions.ServletServerException;

import org.eclipse.jetty.server.Server;

import java.util.logging.Logger;

/**
 * @author Tilen
 */
public class JettyServer implements ServletServer {

    Logger log = Logger.getLogger(JettyServer.class.getSimpleName());

    private Server server;

    public JettyServer() {
    }

    @Override
    public void initServer() {

        server = new Server(8080);

        log.info(getServerName() + " initiated");
    }

    @Override
    public void startServer() {

        if (server == null) {

            String msg = "Jetty has to be initialized before starting it";

            log.severe(msg);

            throw new IllegalStateException(msg);
        }

        if (server.isStarted() || server.isStarting()) {

            String msg = "Jetty is already started";

            log.severe(msg);

            throw new IllegalStateException(msg);
        }

        try {
            server.start();
        } catch (Exception e) {

            log.severe(e.getMessage());

            throw new ServletServerException(e.getMessage(), e.getCause());
        }

        log.info(getServerName() + " started");
    }

    @Override
    public void stopServer() {

        if (server == null) {

            String msg = "Jetty has to be initialized before stopping it";

            log.severe(msg);

            throw new IllegalStateException(msg);
        }

        if (server.isStarted() || server.isStarting()) {

            String msg = "Jetty is already stopped";

            log.severe(msg);

            throw new IllegalStateException(msg);
        }

        try {
            server.stop();
        } catch (Exception e) {

            log.severe(e.getMessage());

            throw new ServletServerException(e.getMessage(), e.getCause());
        }

        log.info(getServerName() + " stopped");
    }

    @Override
    public String getServerName() {

        return "Jetty";
    }
}
