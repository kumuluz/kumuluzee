package com.kumuluz.ee;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.*;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;
import com.kumuluz.ee.common.utils.ResourceUtils;
import com.kumuluz.ee.common.wrapper.ComponentWrapper;
import com.kumuluz.ee.common.wrapper.EeComponentWrapper;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;
import com.kumuluz.ee.loaders.ComponentLoader;
import com.kumuluz.ee.loaders.ServerLoader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

        Map<EeComponentType, EeComponentWrapper> eeComp = new HashMap<>();

        // Wrap components with their metadata and check for duplicates
        for (Component c : components) {

            EeComponentDef def = c.getClass().getDeclaredAnnotation(EeComponentDef.class);

            if (def != null) {

                if (eeComp.containsKey(def.type()) ||
                        Arrays.asList(server.getProvidedEeComponents()).contains(def.type())) {

                    String msg = "Found multiple implementations (" +
                            (eeComp.get(def.type()) != null ? eeComp.get(def.type()).getName() : server.getName()) +
                            ", " + def.name() + ") of the same EE component (" + def.type().getName() + "). " +
                            "Please check to make sure you only include a single implementation of a specific " +
                            "EE component.";

                    log.severe(msg);

                    throw new KumuluzServerException(msg);
                }

                EeComponentDependency[] dependencies = c.getClass().getDeclaredAnnotationsByType(EeComponentDependency.class);
                EeComponentOptional[] optionals = c.getClass().getDeclaredAnnotationsByType(EeComponentOptional.class);

                eeComp.put(def.type(), new EeComponentWrapper(c, def.name(), def.type(), dependencies, optionals));
            }
        }

        log.info("Processing EE component dependencies");

        // Check if all dependencies are fulfilled
        for (EeComponentWrapper cmp : eeComp.values()) {

            for (EeComponentDependency dep : cmp.getDependencies()) {

                String depCompName = null;

                ComponentWrapper depComp = eeComp.get(dep.value());

                // Check all posible locations for the dependency (Components and Server)
                if (depComp != null) {

                    depCompName = depComp.getName();
                } else if (Arrays.asList(server.getProvidedEeComponents()).contains(dep.value())) {

                    depCompName = server.getName();
                }

                if (depCompName == null) {

                    String msg = "EE component dependency unfulfilled. The EE component " + cmp.getType().getName() +
                            " implemented by " + cmp.getName() + " requires " + dep.value().getName() + ", which was not " +
                            "found. Please make sure to include the required component.";

                    log.severe(msg);

                    throw new KumuluzServerException(msg);
                }

                if (dep.implementations().length > 0 &&
                        !Arrays.asList(dep.implementations()).contains(depCompName)) {

                    String msg = "EE component implementation dependency unfulfilled. The EE component " +
                            cmp.getType().getName() + " implemented by " + cmp.getName() + " requires " + dep.value().getName() +
                            " implemented by one of the following implementations: " +
                            Arrays.toString(dep.implementations()) + ". Please make sure you use one of the " +
                            "implementations required by this component.";

                    log.severe(msg);

                    throw new KumuluzServerException(msg);
                }
            }

            // Check if all optional dependencies and their implementations are fulfilled
            for (EeComponentOptional dep : cmp.getOptionalDependencies()) {

                String depCompName = null;

                ComponentWrapper depComp = eeComp.get(dep.value());

                // Check all posible locations for the dependency (Components and Server)
                if (depComp != null) {

                    depCompName = depComp.getName();
                } else if (!Arrays.asList(server.getProvidedEeComponents()).contains(dep.value())) {

                    depCompName = server.getName();
                }

                if (depCompName != null && dep.implementations().length > 0 &&
                        !Arrays.asList(dep.implementations()).contains(depCompName)) {

                    String msg = "EE component implementation dependency unfulfilled. The EE component " +
                            cmp.getType().getName() + "implemented by " + cmp.getName() + " requires " + dep.value().getName() +
                            " implemented by one of the following implementations: " +
                            Arrays.toString(dep.implementations()) + ". Please make sure you use one of the " +
                            "implementations required by this component.";

                    log.severe(msg);

                    throw new KumuluzServerException(msg);
                }
            }
        }

        eeComponents = eeComp.values().stream().collect(Collectors.toList());
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
