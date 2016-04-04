package com.kumuluz.ee.common.utils;

import java.net.URL;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class ResourceUtils {

    public static String getProjectWebResources() {

        URL webapp = ResourceUtils.class.getClassLoader().getResource("webapp");

        if (webapp != null) {

            return webapp.toString();
        }

        return null;
    }

    public static boolean isRunningInJar() {

        URL jar = ResourceUtils.class.getClassLoader().getResource("webapp");

        if (jar == null)
            throw new IllegalStateException("Base resource folder does not exists. Please check " +
                    "your project configuration and make sure you are using maven");

        return jar.toString().toLowerCase().startsWith("jar:");
    }
}
