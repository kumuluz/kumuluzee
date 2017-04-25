package com.kumuluz.ee.jta.narayana;

import com.arjuna.ats.jta.common.JTAEnvironmentBean;
import com.arjuna.ats.jta.common.jtaPropertyManager;
import com.kumuluz.ee.jta.common.TransactionAcquirer;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 * @author Marcos Koch Salvador
 * @since 2.3.0
 */
public class NarayanaTransactionAcquirer implements TransactionAcquirer {

    private JTAEnvironmentBean jtaEnvironment;

    public NarayanaTransactionAcquirer() {
        jtaEnvironment = jtaPropertyManager.getJTAEnvironmentBean();
    }

    @Override
    public UserTransaction getUserTransaction() {
        return jtaEnvironment.getUserTransaction();
    }

    @Override
    public TransactionManager getTransactionManager() {
        return jtaEnvironment.getTransactionManager();
    }

}
