package com.kumuluz.ee.jta.common;

import javax.transaction.Status;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos Koch Salvador
 * @since 2.3.0
 */
public class JtaTransactionHolder {

    public static final List<Integer> TRANSACTION_ACTIVE_STATUS = Arrays.asList(
            Status.STATUS_ACTIVE, Status.STATUS_COMMITTING, Status.STATUS_MARKED_ROLLBACK, Status.STATUS_PREPARED,
            Status.STATUS_PREPARING, Status.STATUS_ROLLING_BACK
    );


    private static final JtaTransactionHolder INSTANCE = new JtaTransactionHolder();
    private TransactionAcquirer transactionAcquirer;

    private JtaTransactionHolder() {
    }

    public static JtaTransactionHolder getInstance() {
        return INSTANCE;
    }

    public void setTransactionAcquirer(TransactionAcquirer transactionAcquirer) {
        this.transactionAcquirer = transactionAcquirer;
    }

    private void validateTransactionAcquirer() {
        Objects.requireNonNull(transactionAcquirer, "TransactionAcquirer not found!");
    }

    public TransactionManager getTransactionManager() {
        validateTransactionAcquirer();
        return transactionAcquirer.getTransactionManager();
    }

    public UserTransaction getUserTransaction() {
        validateTransactionAcquirer();
        return transactionAcquirer.getUserTransaction();
    }

}
