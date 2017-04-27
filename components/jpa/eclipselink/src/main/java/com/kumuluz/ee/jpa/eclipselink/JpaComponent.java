package com.kumuluz.ee.jpa.eclipselink;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;
import com.kumuluz.ee.jpa.common.PersistenceUnitHolder;

import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
@EeComponentDef(name = "EclipseLink", type = EeComponentType.JPA)
public class JpaComponent implements Component {

    private Logger log = Logger.getLogger(JpaComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {

        PersistenceUnitHolder holder = PersistenceUnitHolder.getInstance();

        // Check if JTA is present in the runtime
        Boolean jtaPresent = eeConfig.getEeComponents().stream().anyMatch(c -> c.getType().equals(EeComponentType.JTA));

        holder.setConfigs(eeConfig.getPersistenceConfigs());
        holder.setProviderProperties(new EclipseLinkSettings(jtaPresent));
    }

    @Override
    public void load() {

        log.info("Initiating EclipseLink");
    }
}
