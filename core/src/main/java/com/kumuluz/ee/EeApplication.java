package com.kumuluz.ee;

import com.kumuluz.ee.common.ServletServer;

import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class EeApplication {

    private Logger log = Logger.getLogger(EeApplication.class.getSimpleName());

    private ServletServer server;

    public EeApplication() {

        initialize();
    }

    public void initialize() {

        log.info("Initializing KumuluzEE...");

        ServerLoader sl = new ServerLoader();

        server = sl.loadServletServer();

        server.initServer();
        server.startServer();

        log.info("KumuluzEE started successfully");
    }
}
