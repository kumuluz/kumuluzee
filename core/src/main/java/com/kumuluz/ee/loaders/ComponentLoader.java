package com.kumuluz.ee.loaders;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.config.EeConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * @author Tilen
 */
public class ComponentLoader {

    Logger log = Logger.getLogger(ComponentLoader.class.getSimpleName());

    private ServletServer server;

    private EeConfig eeConfig;

    public ComponentLoader(ServletServer server, EeConfig eeConfig) {

        log.info("Initiating component logger...");

        this.server = server;
        this.eeConfig = eeConfig;
    }

    public void loadComponents() {

        log.info("Loading available components");

        List<Component> components = scanForAvailableComponents();

        for (Component c : components) {

            log.info("Found " + c.getComponentName() + " implemented by " + c
                    .getImplementationName());

            c.init(server, eeConfig);
            c.load();
        }

        log.info("Loading for components complete");
    }

    private List<Component> scanForAvailableComponents() {

        log.finest("Scanning for available components in the runtime");

        List<Component> servers = new ArrayList<>();

        ServiceLoader.load(Component.class).forEach(servers::add);

        return servers;
    }
}
