/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
*/
package com.kumuluz.ee.jta.narayana;

import com.arjuna.ats.arjuna.common.*;
import com.arjuna.ats.jta.common.JTAEnvironmentBean;
import com.arjuna.ats.jta.utils.JNDIManager;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;
import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;
import com.kumuluz.ee.common.utils.StringUtils;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.jta.common.JtaTransactionHolder;

import javax.naming.NamingException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marcos Koch Salvador
 * @since 2.3.0
 */
@EeComponentDef(name = "Narayana JTA", type = EeComponentType.JTA)
public class JtaComponent implements Component {

    private Logger log = Logger.getLogger(JtaComponent.class.getSimpleName());
    private NarayanaConfig narayanaConfig;

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {
        JtaTransactionHolder.getInstance().setTransactionAcquirer(new NarayanaTransactionAcquirer());

        narayanaConfig = new NarayanaConfig();

        ConfigurationUtil cfg = ConfigurationUtil.getInstance();
        Optional<String> narayanaJtaOpt = cfg.get("kumuluzee.jta.narayana");

        if (narayanaJtaOpt.isPresent()) {

            Optional<String> nodeIdentifier = cfg.get("kumuluzee.jta.narayana.node-identifier");
            nodeIdentifier.ifPresent(narayanaConfig::setNodeIdentifier);

            Optional<String> objectStoreDir = cfg.get("kumuluzee.jta.narayana.object-store-dir");
            objectStoreDir.ifPresent(narayanaConfig::setObjectStoreDir);

            Optional<Boolean> commitOnePhase = cfg.getBoolean("kumuluzee.jta.narayana.commit-one-phase");
            commitOnePhase.ifPresent(narayanaConfig::setCommitOnePhase);

            Optional<Integer> defaultTimeout = cfg.getInteger("kumuluzee.jta.narayana.default-timeout");
            defaultTimeout.ifPresent(narayanaConfig::setDefaultTimeout);

            Optional<Integer> periodicRecoveryPeriod = cfg.getInteger("kumuluzee.jta.narayana.periodic-recovery-period");
            periodicRecoveryPeriod.ifPresent(narayanaConfig::setPeriodicRecoveryPeriod);

            Optional<Integer> recoveryBackoffPeriod = cfg.getInteger("kumuluzee.jta.narayana.recovery-backoff-period");
            recoveryBackoffPeriod.ifPresent(narayanaConfig::setRecoveryBackoffPeriod);

            Optional<String> orphanFilters = cfg.get("kumuluzee.jta.narayana.orphan-filters");
            orphanFilters.ifPresent(v -> narayanaConfig.setOrphanFilters( StringUtils.splitToList(v, ",") ));

            Optional<String> recoveryModules = cfg.get("kumuluzee.jta.narayana.recovery-modules");
            recoveryModules.ifPresent(v -> narayanaConfig.setRecoveryModules( StringUtils.splitToList(v, ",") ));

            Optional<String> expiryScanners = cfg.get("kumuluzee.jta.narayana.expiry-scanners");
            expiryScanners.ifPresent(v -> narayanaConfig.setExpiryScanners( StringUtils.splitToList(v, ",") ));
        }
    }

    @Override
    public void load() {
        try {
            log.info("Setuping Narayana JTA");

            getPopulator(CoreEnvironmentBean.class).setNodeIdentifier(narayanaConfig.getNodeIdentifier());

            setObjectStoreDir(narayanaConfig.getObjectStoreDir());

            getPopulator(CoordinatorEnvironmentBean.class).setCommitOnePhase(narayanaConfig.isCommitOnePhase());

            getPopulator(CoordinatorEnvironmentBean.class).setDefaultTimeout(narayanaConfig.getDefaultTimeout());

            getPopulator(RecoveryEnvironmentBean.class).setPeriodicRecoveryPeriod(narayanaConfig.getPeriodicRecoveryPeriod());

            getPopulator(RecoveryEnvironmentBean.class).setRecoveryBackoffPeriod(narayanaConfig.getRecoveryBackoffPeriod());

            getPopulator(JTAEnvironmentBean.class).setXaResourceOrphanFilterClassNames(narayanaConfig.getOrphanFilters());

            getPopulator(RecoveryEnvironmentBean.class).setRecoveryModuleClassNames(narayanaConfig.getRecoveryModules());

            getPopulator(RecoveryEnvironmentBean.class).setExpiryScannerClassNames(narayanaConfig.getExpiryScanners());

            JNDIManager.bindJTAImplementation();
        }  catch (Exception e) {
            throw new KumuluzServerException("There was an error while configuring Narayana", e);
        }
    }

    private void setObjectStoreDir(String objectStoreDir) {
        if (Objects.isNull(objectStoreDir)) {
            return;
        }
        getPopulator(ObjectStoreEnvironmentBean.class).setObjectStoreDir(objectStoreDir);
        getPopulator(ObjectStoreEnvironmentBean.class, "communicationStore").setObjectStoreDir(objectStoreDir);
        getPopulator(ObjectStoreEnvironmentBean.class, "stateStore").setObjectStoreDir(objectStoreDir);
    }

    private <T> T getPopulator(Class<T> beanClass) {
        return BeanPopulator.getDefaultInstance(beanClass);
    }

    private <T> T getPopulator(Class<T> beanClass, String name) {
        return BeanPopulator.getNamedInstance(beanClass, name);
    }
}
