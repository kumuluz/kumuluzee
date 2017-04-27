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
