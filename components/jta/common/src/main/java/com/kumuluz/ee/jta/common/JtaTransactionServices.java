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

import org.jboss.weld.transaction.spi.TransactionServices;

import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

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
