package com.kumuluz.ee.jsp;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.config.EeConfig;

import java.util.logging.Logger;

/**
 * @author Tilen
 */
public class JspComponent implements Component {

    private Logger log = Logger.getLogger(JspComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServer server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating JSP");
    }

    @Override
    public String getImplementationName() {

        return "JSP";
    }
}
