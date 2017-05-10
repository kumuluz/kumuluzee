package com.kumuluz.ee.loaders;

import com.kumuluz.ee.common.Extension;
import com.kumuluz.ee.common.dependencies.EeExtensionDef;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author Jan Meznarič
 * @since 2.3.0
 */
public class ExtensionLoader {

    private static final Logger log = Logger.getLogger(ExtensionLoader.class.getSimpleName());

    private static final Class EXTENSION_ANNOTATIONS[] = {EeExtensionDef.class};

    public static List<Extension> loadExtensions() {

        log.info("Loading available extensions");

        List<Extension> extensions = scanForAvailableExtensions();

        for (Extension e : extensions) {

            boolean anyMatch = Stream.of(EXTENSION_ANNOTATIONS)
                    .map(a -> e.getClass().getDeclaredAnnotation(a))
                    .anyMatch(Objects::nonNull);

            if (!anyMatch) {

                String msg = "The found class \"" + e.getClass().getSimpleName() + "\" is missing an extension" +
                        "definition annotation. The annotation is required in order to correctly process the " +
                        "extension type and its dependencies.";

                log.severe(msg);

                throw new KumuluzServerException(msg);
            }
        }

        log.info("Loading for extensions complete");

        return extensions;
    }

    private static List<Extension> scanForAvailableExtensions() {

        log.finest("Scanning for available extensions in the runtime");

        List<Extension> extensions = new ArrayList<>();

        ServiceLoader.load(Extension.class).forEach(extensions::add);

        return extensions;
    }
}
