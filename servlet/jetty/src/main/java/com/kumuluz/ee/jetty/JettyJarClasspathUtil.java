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
package com.kumuluz.ee.jetty;

import com.kumuluz.ee.loader.EeClassLoader;

import java.util.List;

/**
 * Obtains extra classpath jars from {@link EeClassLoader}. Must be in separate file because {@link EeClassLoader} is
 * not present on the classpath when running in exploded.
 *
 * @author Urban Malc
 * @since 3.1.0
 */
public class JettyJarClasspathUtil {

    public static String getExtraClasspath(List<String> scanLibraries) {

        if (!(JettyJarClasspathUtil.class.getClassLoader() instanceof EeClassLoader)) {
            throw new IllegalStateException("Classloader not instance of EeClassLoader");
        }

        EeClassLoader eeClassLoader = (EeClassLoader) JettyJarClasspathUtil.class.getClassLoader();

        return String.join(",", eeClassLoader
                .getJarFilesLocations(scanLibraries));
    }
}
