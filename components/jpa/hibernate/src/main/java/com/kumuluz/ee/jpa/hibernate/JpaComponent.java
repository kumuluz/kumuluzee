package com.kumuluz.ee.jpa.hibernate;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;
import com.kumuluz.ee.jpa.common.PersistenceUnitHolder;

import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
@EeComponentDef(name = "Hibernate", type = EeComponentType.JPA)
public class JpaComponent implements Component {

    private Logger log = Logger.getLogger(JpaComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {

        PersistenceUnitHolder.getInstance().setConfigs(eeConfig.getPersistenceConfigs());
    }

    @Override
    public void load() {

        log.info("Initiating Hibernate");
    }
}
