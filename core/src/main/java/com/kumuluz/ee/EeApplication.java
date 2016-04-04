package com.kumuluz.ee;

import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.utils.ResourceUtils;
import com.kumuluz.ee.common.wrapper.ComponentWrapper;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;
import com.kumuluz.ee.loaders.ComponentLoader;
import com.kumuluz.ee.loaders.ServerLoader;

import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class EeApplication {

    private Logger log = Logger.getLogger(EeApplication.class.getSimpleName());

    private EeConfig eeConfig;

    private KumuluzServerWrapper server;

    private Map<EeComponentType, ComponentWrapper> components;

    public EeApplication() {

        this.eeConfig = new EeConfig();

        initialize();
    }

    public EeApplication(EeConfig eeConfig) {

        this.eeConfig = eeConfig;

        initialize();
    }

    public static void main(String args[]) {

        EeApplication app = new EeApplication();
    }

    public void initialize() {

        log.info("Initializing KumuluzEE");

        log.info("Checking for requirements");

        checkRequirements();

        log.info("Checks passed");

        ServerLoader sl = new ServerLoader();
        server = sl.loadServletServer();

        ComponentLoader cp = new ComponentLoader(server, eeConfig);

        cp.loadComponents();

        server.getServer().setServerConfig(eeConfig.getServerConfig());
        server.getServer().initServer();

        if (server.getServer() instanceof ServletServer) {

            ServletServer servletServer = (ServletServer) server.getServer();

            servletServer.initWebContext();
        }

        server.getServer().startServer();

        log.info("KumuluzEE started successfully");
    }

    public void checkRequirements() {

        if (ResourceUtils.getProjectWebResources() == null) {

            throw new IllegalStateException("No 'webapp' directory found in the projects " +
                    "resources folder. Please add it to your resources even if it will be empty " +
                    "so that the servlet server can bind to it. If you have added it and still " +
                    "see this error please make sure you have at least one file/class in your " +
                    "projects as some IDEs don't build the project if its empty");
        }

        if (ResourceUtils.isRunningInJar()) {

            throw new RuntimeException("Running in a jar is currently not supported yet. Please " +
                    "build the application with the 'maven-dependency' plugin to explode your app" +
                    " with dependencies. Then run it with 'java -cp " +
                    "target/classes:target/dependency/* yourmainclass'.");
        }
    }
}
