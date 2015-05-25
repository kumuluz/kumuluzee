package com.kumuluz.ee.jaxrs;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;

import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;

/**
 * @author Tilen
 */
public class JaxRsComponent implements Component {

    private Logger log = Logger.getLogger(JaxRsComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServer server) {
    }

    @Override
    public void load() {

        log.info("Initiating Jersey");
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
