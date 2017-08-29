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
package com.kumuluz.ee.loaders;

import com.kumuluz.ee.common.LogsExtension;
import com.kumuluz.ee.common.dependencies.EeExtensionDef;
import com.kumuluz.ee.common.dependencies.EeExtensionGroup;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.4.0
 */
public class LogsExtensionLoader {

    public static Optional<LogsExtension> loadExtension() {

        List<LogsExtension> extensions = scanForAvailableExtensions();

        if (extensions.size() > 1) {

            String implementations = extensions.stream().skip(1)
                    .map(e -> e.getClass().getSimpleName())
                    .reduce(extensions.get(0).getClass().getSimpleName(), (s, e) -> ", " + e.getClass().getSimpleName());

            Logger log = Logger.getLogger(LogsExtensionLoader.class.getSimpleName());

            String msg = "Found multiple implementations (" + implementations + ") of the same EE extension group (logs). " +
                    "Please check to make sure you only include a single implementation of a specific " +
                    "EE extension group.";

            log.severe(msg);

            throw new KumuluzServerException(msg);
        }

        if (extensions.size() == 1) {

            LogsExtension logsExtension = extensions.get(0);

            EeExtensionDef eeExtensionDef = logsExtension.getClass().getDeclaredAnnotation(EeExtensionDef.class);

            if (eeExtensionDef == null) {

                String msg = "The found class \"" + logsExtension.getClass().getSimpleName() + "\" is missing an extension" +
                        "definition annotation. The annotation is required in order to correctly process the " +
                        "extension type and its dependencies.";

                Logger log = Logger.getLogger(LogsExtensionLoader.class.getSimpleName());

                log.severe(msg);

                throw new KumuluzServerException(msg);
            }

            if (!eeExtensionDef.group().equalsIgnoreCase(EeExtensionGroup.LOGS)) {

                String msg = "The found class \"" + logsExtension.getClass().getSimpleName() + "\" does not have the correct " +
                        "extension group defined. The interface \"LogsExtension\" requires that the supporting " +
                        " definition annotations specifies the extension group of \"logs\". ";

                Logger log = Logger.getLogger(LogsExtensionLoader.class.getSimpleName());

                log.severe(msg);

                throw new KumuluzServerException(msg);
            }

            return Optional.of(logsExtension);
        }

        return Optional.empty();
    }

    private static List<LogsExtension> scanForAvailableExtensions() {

        List<LogsExtension> extensions = new ArrayList<>();

        ServiceLoader.load(LogsExtension.class).forEach(extensions::add);

        return extensions;
    }
}
