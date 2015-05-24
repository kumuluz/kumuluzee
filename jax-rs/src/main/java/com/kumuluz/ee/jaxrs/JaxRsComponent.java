package com.kumuluz.ee.jaxrs;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

/**
 * @author Tilen
 */
public class JaxRsComponent implements Component {

    private KumuluzServer server;

    @Override
    public void init(KumuluzServer server) {

        this.server = server;
    }

    @Override
    public void load() {

//        Reflections reflections = new Reflections("");
//
//        Set<Class<?>> annotated =
//                reflections.getTypesAnnotatedWith(ApplicationPath.class);
//
//        System.out.println(annotated.size());

        server.registerServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/api/*");
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
