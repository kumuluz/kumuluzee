package com.kumuluz.ee.common.utils;

import java.net.URL;

/**
 * @author Tilen
 */
public class ResourcesUtils {

    public static String getProjectWebResources() {

        URL webapp = ResourcesUtils.class.getClassLoader().getResource("webapp");

        if (webapp != null) {

            return webapp.toString();
        } else {

            throw new IllegalStateException("No 'webapp' directory found in the projects " +
                    "resources folder. Please add it to your resources even if it will be empty " +
                    "so that the servlet server can bind to it.");
        }
    }
}
