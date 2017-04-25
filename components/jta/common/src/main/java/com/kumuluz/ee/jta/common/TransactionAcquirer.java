package com.kumuluz.ee.jta.common;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 * @author Marcos Koch Salvador
 * @since 2.3.0
 */
public interface TransactionAcquirer {

    UserTransaction getUserTransaction();

    TransactionManager getTransactionManager();

}
