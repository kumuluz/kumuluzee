package com.kumuluz.ee.jta.narayana;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class NarayanaConfig {

    /**
     * Unique transaction manager id.
     */
    private String nodeIdentifier = "1";

    /**
     * Transaction object store directory.
     */
    private String objectStoreDir = "tx-object-store";

    /**
     * Enable one phase commit optimization.
     */
    private boolean commitOnePhase = true;

    /**
     * Transaction timeout in seconds.
     */
    private int defaultTimeout = 60;

    /**
     * Interval in which periodic recovery scans are performed in seconds.
     */
    private int periodicRecoveryPeriod = 120;

    /**
     * Back off period between first and second phases of the recovery scan in seconds.
     */
    private int recoveryBackoffPeriod = 10;

    /**
     * List of orphan filters.
     */
    private List<String> orphanFilters = Arrays.asList(
        "com.arjuna.ats.internal.jta.recovery.arjunacore.JTATransactionLogXAResourceOrphanFilter",
        "com.arjuna.ats.internal.jta.recovery.arjunacore.JTANodeNameXAResourceOrphanFilter"
    );

    /**
     * List of recovery modules.
     */
    private List<String> recoveryModules = Arrays.asList(
        "com.arjuna.ats.internal.arjuna.recovery.AtomicActionRecoveryModule",
        "com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule"
    );

    /**
     * List of expiry scanners.
     */
    private List<String> expiryScanners = Arrays.asList(
        "com.arjuna.ats.internal.arjuna.recovery.AtomicActionRecoveryModule",
        "com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule"
    );

    public String getNodeIdentifier() {
        return nodeIdentifier;
    }

    public void setNodeIdentifier(String nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier;
    }

    public String getObjectStoreDir() {
        return objectStoreDir;
    }

    public void setObjectStoreDir(String objectStoreDir) {
        this.objectStoreDir = objectStoreDir;
    }

    public boolean isCommitOnePhase() {
        return commitOnePhase;
    }

    public void setCommitOnePhase(boolean commitOnePhase) {
        this.commitOnePhase = commitOnePhase;
    }

    public int getDefaultTimeout() {
        return defaultTimeout;
    }

    public void setDefaultTimeout(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public int getPeriodicRecoveryPeriod() {
        return periodicRecoveryPeriod;
    }

    public void setPeriodicRecoveryPeriod(int periodicRecoveryPeriod) {
        this.periodicRecoveryPeriod = periodicRecoveryPeriod;
    }

    public int getRecoveryBackoffPeriod() {
        return recoveryBackoffPeriod;
    }

    public void setRecoveryBackoffPeriod(int recoveryBackoffPeriod) {
        this.recoveryBackoffPeriod = recoveryBackoffPeriod;
    }

    public List<String> getOrphanFilters() {
        if (Objects.isNull(orphanFilters)) {
            orphanFilters = Collections.emptyList();
        }
        return orphanFilters;
    }

    public void setOrphanFilters(List<String> orphanFilters) {
        this.orphanFilters = orphanFilters;
    }

    public List<String> getRecoveryModules() {
        if (Objects.isNull(recoveryModules)) {
            recoveryModules = Collections.emptyList();
        }
        return recoveryModules;
    }

    public void setRecoveryModules(List<String> recoveryModules) {
        this.recoveryModules = recoveryModules;
    }

    public List<String> getExpiryScanners() {
        if (Objects.isNull(expiryScanners)) {
            expiryScanners = Collections.emptyList();
        }
        return expiryScanners;
    }

    public void setExpiryScanners(List<String> expiryScanners) {
        this.expiryScanners = expiryScanners;
    }

}
