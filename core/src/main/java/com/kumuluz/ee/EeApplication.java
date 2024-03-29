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

import com.kumuluz.ee.common.*;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.*;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;
import com.kumuluz.ee.common.filters.PoweredByFilter;
import com.kumuluz.ee.common.runtime.EeRuntime;
import com.kumuluz.ee.common.runtime.EeRuntimeComponent;
import com.kumuluz.ee.common.runtime.EeRuntimeExtension;
import com.kumuluz.ee.common.runtime.EeRuntimeInternal;
import com.kumuluz.ee.common.utils.ResourceUtils;
import com.kumuluz.ee.common.wrapper.ComponentWrapper;
import com.kumuluz.ee.common.wrapper.EeComponentWrapper;
import com.kumuluz.ee.common.wrapper.ExtensionWrapper;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;
import com.kumuluz.ee.configuration.ConfigurationSource;
import com.kumuluz.ee.configuration.utils.ConfigurationImpl;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.factories.AgroalDataSourceFactory;
import com.kumuluz.ee.factories.EeConfigFactory;
import com.kumuluz.ee.loaders.*;
import com.kumuluz.ee.logs.impl.JavaUtilDefaultLogConfigurator;
import io.agroal.api.AgroalDataSource;

import java.util.*;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class EeApplication {

    private Logger log;

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

    public KumuluzServer getServer() {
        return this.server.getServer();
    }

    private void initialize() {

        // Loading the logs extension and set up logging bridges before any actual logging is done
        Optional<LogsExtension> logsExtensionOptional = LogsExtensionLoader.loadExtension();

        LogsExtension logsExtension = null;
        if (logsExtensionOptional.isPresent()) {

            logsExtension = logsExtensionOptional.get();

            Optional<Class<? extends LogManager>> logManager = logsExtension.getJavaUtilLogManagerClass();
            Optional<Handler> logHandler = logsExtension.getJavaUtilLogHandler();

            if (logManager.isPresent()) {
                System.setProperty("java.util.logging.manager", logManager.get().getName());
            } else {
                logHandler.ifPresent(JavaUtilDefaultLogConfigurator::initSoleHandler);
            }
        } else {
            JavaUtilDefaultLogConfigurator.init();
        }

        // Initialize the configuration
        ConfigurationImpl configImpl = new ConfigurationImpl();

        ConfigurationUtil.initialize(configImpl);

        // we need to order config sources here in order to ensure correct initialisation of EeConfig
        configImpl.getConfigurationSources().sort(Comparator.comparingInt(ConfigurationSource::getOrdinal).reversed());

        // initialize config profiles
        String[] configurationProfiles = ConfigurationUtil.getInstance().get("kumuluzee.config.profile")
                .or(() -> ConfigurationUtil.getInstance().get("mp.config.profile"))
                .map(s -> s.split(","))
                .orElse(new String[0]);

        Arrays.stream(configurationProfiles)
                .forEach(profile -> configImpl.getConfigurationSources().forEach(cs -> cs.initProfile(profile)));

        if (this.eeConfig == null) {
            this.eeConfig = EeConfigFactory.buildEeConfig();
        } else if (!EeConfigFactory.isEeConfigValid(this.eeConfig)) {
            throw new KumuluzServerException("The programmatically supplied EeConfig is malformed." +
                    "Please check the supplied values and the config reference to fix the missing or invalid values.");
        }

        EeConfig.initialize(this.eeConfig);

        // We first set java.util.logging.manager by extension detection and only load the extension
        // after configuration sources have been initialized. This is because the property must be set
        // before any calls are made to LogManager or Logger. Some of the external configuration libraries
        // can and do in fact call the methods so this reorder is necessary.
        if (logsExtension!=null) {
            logsExtension.load();
        }

        // Create the main class logger
        log = Logger.getLogger(EeApplication.class.getSimpleName());

        // Signal the configuration impl that logging is up and running
        configImpl.postInit();

        for (ConfigurationSource configurationSource : configImpl.getConfigurationSources()) {
            log.info("Initialized configuration source: " + configurationSource.getClass().getSimpleName());
        }

        log.info("Initializing KumuluzEE");

        log.info("Checking for requirements");

        checkRequirements();

        log.info("Checks passed");

        log.info("Initialized main configuration");

        log.info("Loading available EE components and extensions");

        // Loading the kumuluz server and extracting its metadata
        KumuluzServer kumuluzServer = ServerLoader.loadServletServer();
        processKumuluzServer(kumuluzServer);

        // Loading all the present components, extracting their metadata and process dependencies
        List<Component> components = ComponentLoader.loadComponents();
        List<EeComponentWrapper> eeComponents = processEeComponents(components);

        // Loading the config extensions and extracting its metadata
        List<ConfigExtension> configExtensions = ConfigExtensionLoader.loadExtensions();
        List<ExtensionWrapper<ConfigExtension>> eeConfigExtensions = processSingleEeExtensions(configExtensions, eeComponents);

        List<LogsExtension> logsExtensions = logsExtensionOptional.map(Collections::singletonList).orElseGet(Collections::emptyList);
        List<ExtensionWrapper<LogsExtension>> eeLogsExtensions = processGroupEeExtensions(logsExtensions, eeComponents);

        // Loading the extensions and extracting its metadata
        List<Extension> extensions = ExtensionLoader.loadExtensions();
        List<ExtensionWrapper<Extension>> eeExtensions = processGroupEeExtensions(extensions, eeComponents);

        log.info("EE Components and extensions loaded");

        log.info("Initializing the KumuluzEE runtime");

        EeRuntimeInternal eeRuntimeInternal = new EeRuntimeInternal();

        List<EeRuntimeComponent> eeRuntimeComponents = eeComponents.stream()
                .map(e -> new EeRuntimeComponent(e.getType(), e.getName()))
                .collect(Collectors.toList());

        List<EeRuntimeComponent> serverEeRuntimeComponents = Arrays.stream(server.getProvidedEeComponents())
                .map(c -> new EeRuntimeComponent(c, server.getName()))
                .collect(Collectors.toList());

        serverEeRuntimeComponents.addAll(eeRuntimeComponents);

        eeRuntimeInternal.setEeComponents(serverEeRuntimeComponents);

        List<EeRuntimeExtension> eeRuntimeExtensions = eeExtensions.stream()
                .map(e -> new EeRuntimeExtension(e.getGroup(), e.getName())).collect(Collectors.toList());

        eeRuntimeExtensions.addAll(eeConfigExtensions.stream()
                .map(e -> new EeRuntimeExtension(e.getGroup(), e.getName())).collect(Collectors.toList()));

        eeRuntimeExtensions.addAll(eeLogsExtensions.stream()
                .map(e -> new EeRuntimeExtension(e.getGroup(), e.getName())).collect(Collectors.toList()));

        eeRuntimeInternal.setEeExtensions(eeRuntimeExtensions);

        EeRuntime.initialize(eeRuntimeInternal);

        log.info("Initialized the KumuluzEE runtime");

        // Initiate the config extensions
        log.info("Initializing config extensions");

        for (ExtensionWrapper<ConfigExtension> extension : eeConfigExtensions) {

            log.info("Found config extension implemented by " + extension.getName());

            if (extension.getExtension().isEnabled()) {

                extension.getExtension().load();
                extension.getExtension().init(server, eeConfig);

                List<ConfigurationSource> sources = extension.getExtension().getConfigurationSources();

                if (sources == null || sources.size() == 0) {
                    //noinspection deprecation
                    sources = Collections.singletonList(extension.getExtension().getConfigurationSource());
                }

                for (ConfigurationSource source : sources) {

                    if (source != null) {
                        source.init(configImpl.getDispatcher());
                        configImpl.getConfigurationSources().add(source);
                    }
                }
            } else {
                log.info("Config extension " + extension.getName() + " won't be initialized because it's disabled.");
            }
        }

        // sort the configuration sources again since new sources could be added by extensions
        configImpl.getConfigurationSources().sort(Comparator.comparingInt(ConfigurationSource::getOrdinal).reversed());

        log.info("Config extensions initialized");

        for (ExtensionWrapper<LogsExtension> extension : eeLogsExtensions) {

            log.info("Found logs extension implemented by " + extension.getName());

            extension.getExtension().init(server, eeConfig);
        }

        // Initiate the server
        server.getServer().setServerConfig(eeConfig.getServer());
        server.getServer().initServer();

        // Depending on the server type, initiate server specific functionality
        if (server.getServer() instanceof ServletServer) {

            ServletServer servletServer = (ServletServer) server.getServer();

            List<Extension> allExtensions = new ArrayList<>();
            allExtensions.addAll(eeExtensions.stream().map(ExtensionWrapper::getExtension)
                    .collect(Collectors.toList()));
            allExtensions.addAll(eeConfigExtensions.stream().map(ExtensionWrapper::getExtension)
                    .collect(Collectors.toList()));
            allExtensions.addAll(eeLogsExtensions.stream().map(ExtensionWrapper::getExtension)
                    .collect(Collectors.toList()));
            servletServer.initWebContext(collectScanLibraries(allExtensions));

            // Create and register datasources to the underlying server
            boolean jtaPresent = eeRuntimeInternal.getEeComponents().stream().anyMatch(c -> c.getType().equals(EeComponentType.JTA));

            eeConfig.getDatasources().forEach(dsc -> {
                AgroalDataSource ds = AgroalDataSourceFactory.createDataSource(dsc, jtaPresent);
                servletServer.registerDataSource(ds, dsc.getJndiName());
            });

            eeConfig.getXaDatasources().forEach(xdsc -> {
                AgroalDataSource xds = AgroalDataSourceFactory.createXaDataSource(xdsc, jtaPresent);
                servletServer.registerDataSource(xds, xdsc.getJndiName());
            });

            // Add the server info headers
            if (eeConfig.getServer().getShowServerInfo()) {

                Map<String, String> filterParams = new HashMap<>();
                filterParams.put("name", "KumuluzEE/" + eeRuntimeInternal.getVersion());
                servletServer.registerFilter(PoweredByFilter.class, "/*", filterParams);
            }
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

        for (ExtensionWrapper<Extension> extension : eeExtensions) {

            log.info("Found extension implemented by " + extension.getName());

            if (extension.getExtension().isEnabled()) {
                extension.getExtension().load();
                extension.getExtension().init(server, eeConfig);
            } else {
                log.info("Extension " + extension.getName() + " won't be initialized because it's disabled.");
            }
        }

        log.info("Extensions Initialized");

        server.getServer().startServer();

        log.info("KumuluzEE started successfully");
    }

    private List<String> collectScanLibraries(List<Extension> extensions) {
        Set<String> scanLibraries = new HashSet<>();

        extensions.stream().filter(Extension::isEnabled).forEach(e -> scanLibraries.addAll(e.scanLibraries()));

        return new ArrayList<>(scanLibraries);
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
                            cmp.getType().getName() + " implemented by " + cmp.getName() + " requires " + dep.value()
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

    private <E extends Extension> List<ExtensionWrapper<E>> processGroupEeExtensions(List<E> extensions, List<EeComponentWrapper>
            wrappedComponents) {

        Map<String, ExtensionWrapper<E>> eeExt = new HashMap<>();

        // Wrap extensions with their metadata and check for duplicates
        for (E e : extensions) {

            EeExtensionDef def = e.getClass().getDeclaredAnnotation(EeExtensionDef.class);

            if (def != null) {

                if (eeExt.containsKey(def.group())) {

                    String msg = "Found multiple implementations (" + eeExt.get(def.group()).getName() +
                            ", " + def.name() + ") of the same EE extension group (" + def.group() + "). " +
                            "Please check to make sure you only include a single implementation of a specific " +
                            "EE extension group.";

                    log.severe(msg);

                    throw new KumuluzServerException(msg);
                }

                EeComponentDependency[] dependencies = e.getClass().getDeclaredAnnotationsByType
                        (EeComponentDependency.class);
                EeComponentOptional[] optionals = e.getClass().getDeclaredAnnotationsByType(EeComponentOptional.class);

                eeExt.put(def.group(), new ExtensionWrapper<>(e, def.name(), def.group(), dependencies, optionals));
            }
        }

        List<ExtensionWrapper<E>> extensionWrappers = new ArrayList<>(eeExt.values());

        log.info("Processing EE extension dependencies");

        processEeExtensionDependencies(extensionWrappers, wrappedComponents);

        return extensionWrappers;
    }

    private <E extends Extension> List<ExtensionWrapper<E>> processSingleEeExtensions(List<E> extensions, List<EeComponentWrapper>
            wrappedComponents) {

        List<ExtensionWrapper<E>> extensionWrappers = new ArrayList<>();

        for (E e : extensions) {

            EeExtensionDef def = e.getClass().getDeclaredAnnotation(EeExtensionDef.class);

            if (def != null) {

                EeComponentDependency[] dependencies = e.getClass().getDeclaredAnnotationsByType(EeComponentDependency.class);
                EeComponentOptional[] optionals = e.getClass().getDeclaredAnnotationsByType(EeComponentOptional.class);

                extensionWrappers.add(new ExtensionWrapper<>(e, def.name(), def.group(), dependencies, optionals));
            }
        }

        log.info("Processing EE single extensions dependencies");

        processEeExtensionDependencies(extensionWrappers, wrappedComponents);

        return extensionWrappers;
    }

    private <E extends Extension> void processEeExtensionDependencies(List<ExtensionWrapper<E>> extensions, List<EeComponentWrapper>
            components) {

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

                    String msg = "EE extension implementation dependency unfulfilled. The EE extension group " +
                            ext.getGroup() + " implemented by " + ext.getName() + " requires " + dep.value().getName() +
                            " implemented by one of the following implementations: " +
                            Arrays.toString(dep.implementations()) + ". Please make sure you use one of the " +
                            "implementations required by this component.";

                    log.severe(msg);

                    throw new KumuluzServerException(msg);

                }

                if (dep.implementations().length > 0 &&
                        !Arrays.asList(dep.implementations()).contains(depCompName)) {

                    String msg = "EE extension implementation dependency unfulfilled. The EE extension group " +
                            ext.getGroup() + " implemented by " + ext.getName() + " requires component " +
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

                    String msg = "EE extension optional implementation dependency unfulfilled. The EE extension group " +
                            ext.getGroup() + " implemented by " + ext.getName() + " requires component " +
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

        if (ResourceUtils.isRunningInJar()) {

            log.info("KumuluzEE running inside a JAR runtime.");
        } else {

            log.info("KumuluzEE running in an exploded class and dependency runtime.");
        }
    }
}
