package com.kumuluz.ee;

import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.utils.ResourcesUtils;

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

    public static void main(String args[]) {

        EeApplication app = new EeApplication();
    }

    public void initialize() {

        log.info("Initializing KumuluzEE...");

        log.info("Checking for requirements...");

        checkRequirements();

        log.info("Checks passed.");

        ServerLoader sl = new ServerLoader();

        server = sl.loadServletServer();

        server.initServer();

        server.initWebContext();

        server.startServer();

        log.info("KumuluzEE started successfully");
    }

    public void checkRequirements() {

        if (ResourcesUtils.getProjectWebResources() == null) {

            throw new IllegalStateException("No 'webapp' directory found in the projects " +
                    "resources folder. Please add it to your resources even if it will be empty " +
                    "so that the servlet server can bind to it.");
        }

        if (ResourcesUtils.isRunningInJar()) {

            throw new RuntimeException("Running in a jar is currently not supported yet. Please " +
                    "build the application with the 'maven-dependency' plugin to explode your app" +
                    " with dependencies. Then run it with 'java -cp " +
                    "target/classes:target/dependency/* yourmainclass'.");
        }
    }
}
