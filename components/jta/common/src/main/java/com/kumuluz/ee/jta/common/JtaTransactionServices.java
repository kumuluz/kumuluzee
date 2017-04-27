package com.kumuluz.ee.jta.common;

import org.jboss.weld.transaction.spi.TransactionServices;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marcos Koch Salvador
 * @since 2.3.0
 */
public class JtaTransactionServices implements TransactionServices {

    @Override
    public void registerSynchronization(Synchronization synchronizedObserver) {
        try {
            JtaTransactionHolder.getInstance().getTransactionManager().getTransaction().registerSynchronization(synchronizedObserver);
        } catch (IllegalStateException | javax.transaction.RollbackException | SystemException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isTransactionActive() {
        try {
            int status = JtaTransactionHolder.getInstance().getTransactionManager().getStatus();
            return JtaTransactionHolder.TRANSACTION_ACTIVE_STATUS.contains(status);
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserTransaction getUserTransaction() {
        return JtaTransactionHolder.getInstance().getUserTransaction();
    }

    @Override
    public void cleanup() {
    }
}
