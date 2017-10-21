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
package com.kumuluz.ee.jta.common;

import javax.transaction.Status;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
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

    public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
        validateTransactionAcquirer();
        return transactionAcquirer.getTransactionSynchronizationRegistry();
    }
}
