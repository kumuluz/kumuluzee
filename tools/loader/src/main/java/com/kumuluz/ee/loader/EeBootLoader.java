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
package com.kumuluz.ee.loader;

import com.kumuluz.ee.loader.exception.EeClassLoaderException;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Benjamin Kastelic
 *
 */
public class EeBootLoader {

    public static void main(String[] args) throws Throwable {

        try {
            ResourceBundle bootLoaderProperties = ResourceBundle.getBundle("META-INF/kumuluzee/boot-loader");

            String mainClass = bootLoaderProperties.getString("main-class");

            launch(args, mainClass);
        } catch (MissingResourceException e) {

            throw new EeClassLoaderException("KumuluzEE Boot Loader config properties are malformed or missing.", e);
        }
    }

    /**
     * Start the boot procedure.
     * Use the {@link EeClassLoader} EeClassLoader to find, load and start the main class.
     */
    private static void launch(String[] args, String mainClass) throws Throwable {
        EeClassLoader classLoader = new EeClassLoader();
        classLoader.invokeMain(mainClass, args);
    }
}
