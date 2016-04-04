package com.kumuluz.ee;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.*;
import com.kumuluz.ee.common.utils.ResourceUtils;
import com.kumuluz.ee.common.wrapper.ComponentWrapper;
import com.kumuluz.ee.common.wrapper.EeComponentWrapper;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;
import com.kumuluz.ee.loaders.ComponentLoader;
import com.kumuluz.ee.loaders.ServerLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class EeApplication {

    private Logger log = Logger.getLogger(EeApplication.class.getSimpleName());

    private EeConfig eeConfig;

    private KumuluzServerWrapper server;

    private List<EeComponentWrapper> eeComponents;

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

        log.info("Initializing components");

        // Loading the kumuluz server and extracting its metadata
        KumuluzServer kumuluzServer = ServerLoader.loadServletServer();
        processKumuluzServer(kumuluzServer);

        // Loading all the present components, extracting their metadata and process dependencies
        List<Component> components = ComponentLoader.loadComponents();
        processEeComponents(components);

        // Initiate the server
        server.getServer().setServerConfig(eeConfig.getServerConfig());
        server.getServer().initServer();

        // Depending on the server type, initiate server specific functionality
        if (server.getServer() instanceof ServletServer) {

            ServletServer servletServer = (ServletServer) server.getServer();

            servletServer.initWebContext();
        }

        // Initiate every found component in the order specified by the components dependencies
        for (EeComponentWrapper cw : eeComponents) {

            log.info("Found EE component " + cw.getType().getName() + " implemented by " + cw.getName());

            cw.getComponent().init(server, eeConfig);
            cw.getComponent().load();
        }

        log.info("Components initialized");

        server.getServer().startServer();

        log.info("KumuluzEE started successfully");
    }

    private void processKumuluzServer(KumuluzServer kumuluzServer) {

        ServerDef serverDef = kumuluzServer.getClass().getDeclaredAnnotation(ServerDef.class);

        server = new KumuluzServerWrapper(kumuluzServer, serverDef.value(), serverDef.provides());
    }

    private void processEeComponents(List<Component> components) {

        List<EeComponentWrapper> eeComp = new ArrayList<>();

        for (Component c : components) {

            EeComponentDef def = c.getClass().getDeclaredAnnotation(EeComponentDef.class);

            if (def != null) {

                EeComponentDependency[] dependencies = c.getClass().getDeclaredAnnotationsByType(EeComponentDependency.class);
                EeComponentOptional[] optionals = c.getClass().getDeclaredAnnotationsByType(EeComponentOptional.class);

                eeComponents.add(new EeComponentWrapper(c, def.name(), def.type(), dependencies, optionals));
            }
        }

        eeComponents = eeComp;
    }

    private void checkRequirements() {

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
