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
package com.kumuluz.ee;

import com.kumuluz.ee.builders.JtaXADataSourceBuilder;
import com.kumuluz.ee.common.*;
import com.kumuluz.ee.common.config.DataSourceConfig;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.config.XaDataSourceConfig;
import com.kumuluz.ee.common.datasources.XADataSourceBuilder;
import com.kumuluz.ee.common.datasources.NonJtaXADataSourceWrapper;
import com.kumuluz.ee.common.datasources.XADataSourceWrapper;
import com.kumuluz.ee.common.dependencies.*;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;
import com.kumuluz.ee.common.filters.PoweredByFilter;
import com.kumuluz.ee.common.utils.ResourceUtils;
import com.kumuluz.ee.common.wrapper.*;
import com.kumuluz.ee.configuration.ConfigurationSource;
import com.kumuluz.ee.configuration.utils.ConfigurationImpl;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.loaders.ComponentLoader;
import com.kumuluz.ee.loaders.ConfigExtensionLoader;
import com.kumuluz.ee.loaders.ExtensionLoader;
import com.kumuluz.ee.loaders.ServerLoader;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.XADataSource;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class EeApplication {

    private Logger log = Logger.getLogger(EeApplication.class.getSimpleName());

    private EeConfig eeConfig;

    private KumuluzServerWrapper server;

    public EeApplication() {

        initialize();
    }

    public EeApplication(EeConfig eeConfig) {

        this.eeConfig = eeConfig;

        initialize();
    }

    public static void main(String args[]) {

        EeApplication app = new EeApplication();
    }

    private void initialize() {

        log.info("Initializing KumuluzEE");

        log.info("Checking for requirements");

        checkRequirements();

        log.info("Checks passed");

        log.info("Initializing main configuration");

        ConfigurationImpl configImpl = new ConfigurationImpl();

        ConfigurationUtil.initialize(configImpl);

        if (this.eeConfig == null) {
            this.eeConfig = new EeConfig();
            eeConfig.init();
        }

        log.info("Initialized main config");

        // Loading the kumuluz server and extracting its metadata
        KumuluzServer kumuluzServer = ServerLoader.loadServletServer();
        processKumuluzServer(kumuluzServer);

        // Loading all the present components, extracting their metadata and process dependencies
        List<Component> components = ComponentLoader.loadComponents();
        List<EeComponentWrapper> eeComponents = processEeComponents(components);

        eeConfig.getEeComponents().addAll(eeComponents);

        // Loading the config extensions and extracting its metadata
        List<ConfigExtension> configExtensions = ConfigExtensionLoader.loadExtensions();
        List<ConfigExtensionWrapper> eeConfigExtensions = processEeConfigExtensions(configExtensions, eeComponents);

        // Loading the extensions and extracting its metadata
        List<Extension> extensions = ExtensionLoader.loadExtensions();
        List<ExtensionWrapper> eeExtensions = processEeExtensions(extensions, eeComponents);

        // Initiate the config extensions
        log.info("Initializing config extensions");

        for (ConfigExtensionWrapper extension : eeConfigExtensions) {

            log.info("Found config extension implemented by " + extension.getClass().getDeclaredAnnotation
                    (EeExtensionDef.class).name());

            extension.getExtension().init(server, eeConfig);
            extension.getExtension().load();

            ConfigurationSource source = extension.getExtension().getConfigurationSource();

            source.init(configImpl.getDispatcher());
            configImpl.getConfigurationSources().add(1, source);
        }

        log.info("Config extensions initialized");

        // Initiate the server
        server.getServer().setServerConfig(eeConfig.getServerConfig());
        server.getServer().initServer();

        // Depending on the server type, initiate server specific functionality
        if (server.getServer() instanceof ServletServer) {

            ServletServer servletServer = (ServletServer) server.getServer();

            servletServer.initWebContext();

            // Create and register datasources to the underlying server
            if (eeConfig.getDatasources().size() > 0) {

                for (DataSourceConfig dsc : eeConfig.getDatasources()) {

                    HikariDataSource ds = new HikariDataSource();
                    ds.setJdbcUrl(dsc.getConnectionUrl());
                    ds.setUsername(dsc.getUsername());
                    ds.setPassword(dsc.getPassword());

                    if (dsc.getDriverClass() != null && !dsc.getDriverClass().isEmpty())
                        ds.setDriverClassName(dsc.getDriverClass());

                    if (dsc.getMaxPoolSize() != null)
                        ds.setMaximumPoolSize(dsc.getMaxPoolSize());

                    servletServer.registerDataSource(ds, dsc.getJndiName());
                }
            }

            if (eeConfig.getXaDatasources().size() > 0) {

                Boolean jtaPresent = eeConfig.getEeComponents().stream().anyMatch(c -> c.getType().equals(EeComponentType.JTA));

                for (XaDataSourceConfig xdsc : eeConfig.getXaDatasources()) {

                    XADataSourceBuilder XADataSourceBuilder = new XADataSourceBuilder(xdsc);

                    XADataSource xaDataSource = XADataSourceBuilder.constructXaDataSource();

                    XADataSourceWrapper xaDataSourceWrapper;

                    if (jtaPresent) {
                        xaDataSourceWrapper = JtaXADataSourceBuilder.buildJtaXADataSourceWrapper(xaDataSource);
                    } else {
                        xaDataSourceWrapper = new NonJtaXADataSourceWrapper(xaDataSource);
                    }

                    servletServer.registerDataSource(xaDataSourceWrapper, xdsc.getJndiName());
                }
            }

            // Add all included filters
            Map<String, String> filterParams = new HashMap<>();
            filterParams.put("name", "KumuluzEE/" + eeConfig.getVersion());
            servletServer.registerFilter(PoweredByFilter.class, "/*", filterParams);
        }

        log.info("Initializing components");

        // Initiate every found component in the order specified by the components dependencies
        for (EeComponentWrapper cw : eeComponents) {

            log.info("Found EE component " + cw.getType().getName() + " implemented by " + cw.getName());

            cw.getComponent().init(server, eeConfig);
            cw.getComponent().load();
        }

        log.info("Components initialized");

        // Initiate the other extensions
        log.info("Initializing extensions");

        for (ExtensionWrapper extension : eeExtensions) {

            log.info("Found extension implemented by " + extension.getClass()
                    .getDeclaredAnnotation(EeExtensionDef.class).name());

            extension.getExtension().init(server, eeConfig);
            extension.getExtension().load();
        }

        log.info("Extensions Initialized");

        server.getServer().startServer();

        log.info("KumuluzEE started successfully");
    }

    private void processKumuluzServer(KumuluzServer kumuluzServer) {

        ServerDef serverDef = kumuluzServer.getClass().getDeclaredAnnotation(ServerDef.class);

        server = new KumuluzServerWrapper(kumuluzServer, serverDef.value(), serverDef.provides());
    }

    private List<EeComponentWrapper> processEeComponents(List<Component> components) {

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

                EeComponentDependency[] dependencies = c.getClass().getDeclaredAnnotationsByType
                        (EeComponentDependency.class);
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
                            " implemented by " + cmp.getName() + " requires " + dep.value().getName() + ", which was " +
                            "not " +
                            "found. Please make sure to include the required component.";

                    log.severe(msg);

                    throw new KumuluzServerException(msg);
                }

                if (dep.implementations().length > 0 &&
                        !Arrays.asList(dep.implementations()).contains(depCompName)) {

                    String msg = "EE component implementation dependency unfulfilled. The EE component " +
                            cmp.getType().getName() + " implemented by " + cmp.getName() + " requires " + dep.value()
                            .getName() +
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

                    String msg = "EE component optional implementation dependency unfulfilled. The EE component " +
                            cmp.getType().getName() + "implemented by " + cmp.getName() + " requires " + dep.value()
                            .getName() +
                            " implemented by one of the following implementations: " +
                            Arrays.toString(dep.implementations()) + ". Please make sure you use one of the " +
                            "implementations required by this component.";

                    log.severe(msg);

                    throw new KumuluzServerException(msg);
                }
            }
        }

        return new ArrayList<>(eeComp.values());
    }

    private List<ExtensionWrapper> processEeExtensions(List<Extension> extensions, List<EeComponentWrapper> wrappedComponents) {

        Map<EeExtensionType, ExtensionWrapper> eeExt = new HashMap<>();

        // Wrap extensions with their metadata and check for duplicates
        for (Extension e : extensions) {

            EeExtensionDef def = e.getClass().getDeclaredAnnotation(EeExtensionDef.class);

            if (def != null) {

                if (eeExt.containsKey(def.type())) {

                    String msg = "Found multiple implementations (" + eeExt.get(def.type()).getName() +
                            ", " + def.name() + ") of the same EE extension (" + def.type().getName() + "). " +
                            "Please check to make sure you only include a single implementation of a specific " +
                            "EE extension.";

                    log.severe(msg);

                    throw new KumuluzServerException(msg);
                }

                EeComponentDependency[] dependencies = e.getClass().getDeclaredAnnotationsByType
                        (EeComponentDependency.class);
                EeComponentOptional[] optionals = e.getClass().getDeclaredAnnotationsByType(EeComponentOptional.class);

                eeExt.put(def.type(), new ExtensionWrapper(e, def.name(), def.type(), dependencies, optionals));
            }
        }

        List<ExtensionWrapper> extensionWrappers = new ArrayList<>(eeExt.values());

        log.info("Processing EE extension dependencies");

        processEeExtensionDependencies(extensionWrappers, wrappedComponents);

        return extensionWrappers;
    }

    private List<ConfigExtensionWrapper> processEeConfigExtensions(List<ConfigExtension> extensions, List<EeComponentWrapper> wrappedComponents) {

        List<ConfigExtensionWrapper> extensionWrappers = new ArrayList<>();

        for (ConfigExtension c : extensions) {

            EeExtensionDef def = c.getClass().getDeclaredAnnotation(EeExtensionDef.class);

            if (def != null) {

                EeComponentDependency[] dependencies = c.getClass().getDeclaredAnnotationsByType(EeComponentDependency.class);
                EeComponentOptional[] optionals = c.getClass().getDeclaredAnnotationsByType(EeComponentOptional.class);

                extensionWrappers.add(new ConfigExtensionWrapper(c, def.name(), def.type(), dependencies, optionals));
            }
        }

        log.info("Processing EE config extension dependencies");

        processEeExtensionDependencies(extensionWrappers, wrappedComponents);

        return extensionWrappers;
    }

    private void processEeExtensionDependencies(List<? extends ExtensionWrapper> extensions, List<EeComponentWrapper> components) {

        // Check if all dependencies are fulfilled
        for (ExtensionWrapper ext : extensions) {

            for (EeComponentDependency dep : ext.getDependencies()) {

                Optional<EeComponentWrapper> depComp = components.stream()
                        .filter(c -> c.getType().equals(dep.value())).findFirst();

                String depCompName = null;

                if (depComp.isPresent()) {

                    depCompName = depComp.get().getName();
                } else if (Arrays.asList(server.getProvidedEeComponents()).contains(dep.value())) {

                    depCompName = server.getName();
                }

                if (depCompName == null) {

                    String msg = "EE extension implementation dependency unfulfilled. The EE extension " +
                            ext.getType().getName() + " requires " + dep.value().getName() +
                            " implemented by one of the following implementations: " +
                            Arrays.toString(dep.implementations()) + ". Please make sure you use one of the " +
                            "implementations required by this component.";

                    log.severe(msg);

                    throw new KumuluzServerException(msg);

                }

                if (dep.implementations().length > 0 &&
                        !Arrays.asList(dep.implementations()).contains(depCompName)) {

                    String msg = "EE extension implementation dependency unfulfilled. The EE extension " +
                            ext.getType().getName() + " implemented by " + ext.getName() + " requires component " +
                            dep.value().getName() + " implemented by one of the following implementations: " +
                            Arrays.toString(dep.implementations()) + ". Please make sure you use one of the " +
                            "component implementations required by this component.";

                    log.severe(msg);

                    throw new KumuluzServerException(msg);
                }
            }

            // Check if all optional dependencies and their implementations are fulfilled
            for (EeComponentOptional dep : ext.getOptionalDependencies()) {

                Optional<EeComponentWrapper> depComp = components.stream()
                        .filter(c -> c.getType().equals(dep.value())).findFirst();

                String depCompName = null;

                // Check all posible locations for the dependency (Components and Server)
                if (depComp.isPresent()) {

                    depCompName = depComp.get().getName();
                } else if (!Arrays.asList(server.getProvidedEeComponents()).contains(dep.value())) {

                    depCompName = server.getName();
                }

                if (depCompName != null && dep.implementations().length > 0 &&
                        !Arrays.asList(dep.implementations()).contains(depCompName)) {

                    String msg = "EE extension optional implementation dependency unfulfilled. The EE extension " +
                            ext.getType().getName() + "implemented by " + ext.getName() + " requires component " +
                            dep.value().getName() + " implemented by one of the following implementations: " +
                            Arrays.toString(dep.implementations()) + ". Please make sure you use one of the " +
                            "component implementations required by this component.";

                    log.severe(msg);

                    throw new KumuluzServerException(msg);
                }
            }
        }
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

            log.info("KumuluzEE running inside a JAR runtime.");
        } else {

            log.info("KumuluzEE running in an exploded class and dependency runtime.");
        }
    }
}
