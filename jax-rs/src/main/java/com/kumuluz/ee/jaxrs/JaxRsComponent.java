package com.kumuluz.ee.jaxrs;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.exceptions.ComponentsException;
import com.kumuluz.ee.common.utils.ClassUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;

/**
 * @author Tilen
 */
public class JaxRsComponent implements Component {

    private Logger log = Logger.getLogger(JaxRsComponent.class.getSimpleName());

    private KumuluzServer server;

    @Override
    public void init(KumuluzServer server) {

        this.server = server;
    }

    @Override
    public void load() {

        List<String> jaxRsApplications = ClassUtils.getClassNamesWithAnnotation(ApplicationPath
                .class);

        log.info("Scanning for JAX-RS applications");

        for (String jaxRsApp : jaxRsApplications) {

            Class<?> c = ClassUtils.loadClass(jaxRsApp);

            if (c == null) {

                throw new ComponentsException("Class '" + jaxRsApp + "' was found during " +
                        "annotation scanning however it could not be loaded by the class loader");
            }

            Map<String, String> parameters = new HashMap<>();
            parameters.put(JaxRsParameters.APPLICATION, c.getCanonicalName());

            String pattern = c.getAnnotation(ApplicationPath.class).value();

            if (pattern.endsWith("/")) pattern += "*";
            else if (!pattern.endsWith("/*")) pattern += "/*";

            log.info("Initiating JAX-RS application: " + c.getCanonicalName() + " with pattern: "
                    + pattern);

            server.registerServlet(org.glassfish.jersey.servlet.ServletContainer.class,
                    pattern, parameters);
        }
    }

    @Override
    public String getComponentName() {

        return "JAX-RS";
    }

    @Override
    public String getImplementationName() {

        return "Jersey";
    }
}
