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
package com.kumuluz.ee.common.utils;

import com.kumuluz.ee.common.config.DevConfig;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class ResourceUtils {

    public static String getProjectWebResources() {

        // First check the `webapp` directory in the compiled resources
        URL webApp = ResourceUtils.class.getClassLoader().getResource("webapp");

        if (webApp != null) {

            return webApp.toString();
        }

        // Next check if running inside an IDE and try to find the `src/main/webapp` dir
        URL resourceRoot = ResourceUtils.class.getClassLoader().getResource(".");

        if (resourceRoot != null) {

            try {

                Path resourceRootPath = Paths.get(resourceRoot.toURI());

                // If running with maven
                if (Files.isDirectory(resourceRootPath) && resourceRootPath.getFileName().toString().equals("classes")
                        && resourceRootPath.getParent() != null && resourceRootPath.getParent().getFileName().toString().equals("target")) {

                    DevConfig devConfig = EeConfig.getInstance().getDev();

                    Path sibling = devConfig.getWebappDir() == null ?
                            Paths.get( "src", "main", "webapp") :
                            Paths.get(devConfig.getWebappDir());

                    Path sourceWebApp = resourceRootPath.getParent().resolveSibling(sibling);

                    if (Files.isDirectory(sourceWebApp)) {

                        return sourceWebApp.toString();
                    }
                }
            } catch (URISyntaxException e) {

                throw new KumuluzServerException("Could not retrieve the class loaders' resource dir.", e);
            }
        }

        // Finally if nothing is found, create a temp directory and delete it on shutdown
        try {

            final Path tempWebApp = Files.createTempDirectory("kumuluzee-tmp-webapp");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {

                try {
                    Files.delete(tempWebApp);
                } catch (IOException ignored) {
                }
            }));

            return tempWebApp.toString();
        } catch (IOException e) {

            throw new KumuluzServerException("Could not initialize a temporary webapp directory.", e);
        }
    }

    public static boolean isRunningInJar() {

        URL jar = ResourceUtils.class.getClassLoader().getResource("webapp");

        return (jar == null || jar.toString().toLowerCase().startsWith("jar:"))
                && ResourceUtils.class.getClassLoader().getClass().getName().equals("com.kumuluz.ee.loader.EeClassLoader");
    }
}
