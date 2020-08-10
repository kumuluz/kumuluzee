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

import com.kumuluz.ee.common.exceptions.KumuluzServerException;
import io.agroal.api.transaction.TransactionIntegration;

import javax.transaction.Status;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author Marcos Koch Salvador
 * @since 2.3.0
 */
public abstract class JtaProvider {

    public static final List<Integer> TRANSACTION_ACTIVE_STATUS = Arrays.asList(
            Status.STATUS_ACTIVE, Status.STATUS_COMMITTING, Status.STATUS_MARKED_ROLLBACK, Status.STATUS_PREPARED,
            Status.STATUS_PREPARING, Status.STATUS_ROLLING_BACK
    );

    private static JtaProvider instance;

    public static JtaProvider getInstance() {
        if (instance == null) {
            Iterator<JtaProvider> it = ServiceLoader.load(JtaProvider.class).iterator();

            if (!it.hasNext()) {
                throw new KumuluzServerException("No JTA components were found");
            }

            instance = it.next();
        }
        return instance;
    }

    public abstract UserTransaction getUserTransaction();

    public abstract TransactionManager getTransactionManager();

    public abstract TransactionSynchronizationRegistry getTransactionSynchronizationRegistry();

    public abstract TransactionIntegration getTransactionIntegration();
}
