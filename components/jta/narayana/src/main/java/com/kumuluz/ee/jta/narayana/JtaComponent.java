package com.kumuluz.ee.jta.narayana;

import com.arjuna.ats.arjuna.common.arjPropertyManager;
import com.arjuna.ats.jta.utils.JNDIManager;
import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;
import com.kumuluz.ee.jta.common.JtaTransactionHolder;

import javax.naming.NamingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Marcos Koch Salvador
 * @since 2.3.0
 */
@EeComponentDef(name = "Narayana JTA", type = EeComponentType.JTA)
public class JtaComponent implements Component {

    private Logger log = Logger.getLogger(JtaComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {
        JtaTransactionHolder.getInstance().setTransactionAcquirer(new NarayanaTransactionAcquirer());
    }

    @Override
    public void load() {
        log.info("Initiating Narayana JTA");

        arjPropertyManager.getObjectStoreEnvironmentBean().setObjectStoreDir("narayana");

        try {
            JNDIManager.bindJTAImplementation();
        } catch (NamingException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
