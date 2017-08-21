package com.kumuluz.ee.jta.common.utils;

import com.kumuluz.ee.jta.common.JtaTransactionHolder;
import com.kumuluz.ee.jta.common.exceptions.CannotRetrieveTxException;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

public class TxUtils {

    public static Boolean isActive(TransactionManager transactionManager) {

        try {
            Transaction tx = transactionManager.getTransaction();

            return tx != null && JtaTransactionHolder.TRANSACTION_ACTIVE_STATUS.contains(tx.getStatus());
        } catch (SystemException e) {
            throw new CannotRetrieveTxException(e);
        }
    }
}
