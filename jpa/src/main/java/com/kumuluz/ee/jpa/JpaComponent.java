package com.kumuluz.ee.jpa;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;

import java.util.logging.Logger;

/**
 * @author Tilen
 */
public class JpaComponent implements Component {

    private Logger log = Logger.getLogger(JpaComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServer server) {
    }

    @Override
    public void load() {

        log.info("Initiating EclipseLink");
    }

    @Override
    public String getComponentName() {

        return "JPA";
    }

    @Override
    public String getImplementationName() {

        return "EclipseLink";
    }
}
