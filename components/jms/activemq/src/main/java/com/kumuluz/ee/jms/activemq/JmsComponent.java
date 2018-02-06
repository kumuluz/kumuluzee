package com.kumuluz.ee.jms.activemq;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;

import java.util.logging.Logger;

/**
 * @author Dejan Ognjenović
 * @since 2.5.0
 */
@EeComponentDef(name = "ActiveMQ", type = EeComponentType.JMS)
public class JmsComponent implements Component {

    private Logger log = Logger.getLogger(JmsComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {

    }

    @Override
    public void load() {

        log.info("Initiating ActiveMQ");
    }

}
