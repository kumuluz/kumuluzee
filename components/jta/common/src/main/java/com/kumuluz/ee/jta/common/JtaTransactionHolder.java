package com.kumuluz.ee.jta.common;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.util.Objects;

/**
 * @author Marcos Koch Salvador
 * @since 2.3.0
 */
public class JtaTransactionHolder {

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
