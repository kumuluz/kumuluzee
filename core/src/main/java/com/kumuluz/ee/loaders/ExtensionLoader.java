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

import com.kumuluz.ee.common.Extension;
import com.kumuluz.ee.common.dependencies.EeExtensionDef;
import com.kumuluz.ee.common.dependencies.EeExtensionType;
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

    public static List<Extension> loadExtensions() {

        log.info("Loading available extensions");

        List<Extension> extensions = scanForAvailableExtensions();

        for (Extension e : extensions) {

            EeExtensionDef eeExtensionDef = e.getClass().getDeclaredAnnotation(EeExtensionDef.class);

            if (eeExtensionDef == null) {

                String msg = "The found class \"" + e.getClass().getSimpleName() + "\" is missing an extension" +
                        "definition annotation. The annotation is required in order to correctly process the " +
                        "extension type and its dependencies.";

                log.severe(msg);

                throw new KumuluzServerException(msg);
            }

            if (eeExtensionDef.type().equals(EeExtensionType.CONFIG)) {

                String msg = "The found class \"" + e.getClass().getSimpleName() + "\" does not have the correct " +
                        "extension type defined. The extension type \"CONFIG\" requires that the class implements " +
                        "the \"ConfigExtension\" interface.";

                log.severe(msg);

                throw new KumuluzServerException(msg);
            }
        }

        log.info("Extension loading complete");

        return extensions;
    }

    private static List<Extension> scanForAvailableExtensions() {

        log.finest("Scanning for available extensions in the runtime");

        List<Extension> extensions = new ArrayList<>();

        ServiceLoader.load(Extension.class).forEach(extensions::add);

        return extensions;
    }
}
