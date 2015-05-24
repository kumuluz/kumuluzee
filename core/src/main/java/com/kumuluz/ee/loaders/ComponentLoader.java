package com.kumuluz.ee.loaders;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.exceptions.ComponentsException;
import com.kumuluz.ee.common.utils.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Tilen
 */
public class ComponentLoader {

    Logger log = Logger.getLogger(ComponentLoader.class.getSimpleName());

    private static final String[] availableComponents = {
            "com.kumuluz.ee.jaxrs.JaxRsComponent"
    };

    private ServletServer server;

    public ComponentLoader(ServletServer server) {

        log.info("Initiating component logger...");

        this.server = server;
    }

    public void loadComponents() {

        log.info("Loading available components");

        List<Class<?>> components = scanForAvailableComponents();

        for (Class<?> c : components) {

            Component comp;

            try {

                comp = (Component) c.newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassCastException e) {

                log.severe("Failed to instantiate: " + c.getSimpleName());

                throw new ComponentsException(e.getMessage(), e.getCause());
            }

            log.info("Found " + comp.getComponentName() + " implemented by " + comp
                    .getImplementationName());

            comp.init(server);
            comp.load();
        }

        log.info("Loading for components complete");
    }

    private List<Class<?>> scanForAvailableComponents() {

        log.finest("Scanning for available components in the runtime");

        ArrayList<Class<?>> servers = new ArrayList<>();

        Arrays.stream(availableComponents)
                .filter(ClassUtils::isPresent)
                .forEach(c -> servers.add(ClassUtils.loadClass(c)));

        return servers;
    }
}
