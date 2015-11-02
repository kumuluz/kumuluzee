package com.kumuluz.ee.jaxrs;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.config.EeConfig;
import org.kohsuke.MetaInfServices;

import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;

/**
 * @author Tilen
 */
@MetaInfServices
public class JaxRsComponent implements Component {

    private Logger log = Logger.getLogger(JaxRsComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServer server, EeConfig eeConfig) {
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
