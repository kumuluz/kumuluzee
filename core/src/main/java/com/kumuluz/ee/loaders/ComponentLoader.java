package com.kumuluz.ee.loaders;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;
import com.kumuluz.ee.common.wrapper.ComponentWrapper;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class ComponentLoader {

    private static final Logger log = Logger.getLogger(ComponentLoader.class.getSimpleName());

    private static final Class COMPONENT_ANNOTATIONS[] = { EeComponentDef.class };

    public static List<Component> loadComponents() {

        log.info("Loading available components");

        List<Component> components = scanForAvailableComponents();

        for (Component c : components) {

            boolean anyMatch = Stream.of(COMPONENT_ANNOTATIONS)
                    .map(a -> (Annotation) c.getClass().getDeclaredAnnotation(a))
                    .anyMatch(a -> a != null);

            if (!anyMatch) {

                String msg = "The found class \"" + c.getClass().getSimpleName()  + "\" is missing a component" +
                        "definition annotation. The annotation is required in order to correctly process the component" +
                        "type and its dependencies.";

                log.severe(msg);

                throw new KumuluzServerException(msg);
            }
        }

        log.info("Loading for components complete");

        return components;
    }

    private static List<Component> scanForAvailableComponents() {

        log.finest("Scanning for available components in the runtime");

        List<Component> components = new ArrayList<>();

        ServiceLoader.load(Component.class).forEach(components::add);

        return components;
    }
}
