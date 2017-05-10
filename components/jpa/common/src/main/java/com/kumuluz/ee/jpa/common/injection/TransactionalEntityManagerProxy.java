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
package com.kumuluz.ee.jpa.common.injection;

import com.kumuluz.ee.jta.common.JtaTransactionHolder;

import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public class TransactionalEntityManagerProxy implements InvocationHandler {

    private EntityManager em;
    private TransactionManager manager;

    private TransactionalEntityManagerProxy(EntityManager em) {
        this.em = em;
        this.manager = JtaTransactionHolder.getInstance().getTransactionManager();
    }

    public static Object newInstance(EntityManager em) {
        return Proxy.newProxyInstance(em.getClass().getClassLoader(), new Class<?>[]{EntityManager.class}, new TransactionalEntityManagerProxy(em));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object result;

        try {

            if (!em.isJoinedToTransaction() &&
                    JtaTransactionHolder.TRANSACTION_ACTIVE_STATUS.contains(manager.getStatus())) {
                em.joinTransaction();
            }

            result = method.invoke(em, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        }

        return result;
    }
}
