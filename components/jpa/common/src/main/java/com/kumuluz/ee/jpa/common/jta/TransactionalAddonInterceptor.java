package com.kumuluz.ee.jpa.common.jta;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 300)
@TransactionalAddon
public class TransactionalAddonInterceptor {

    @Inject
    private TransactionalAddonContext transactionalAddonContext;

    @AroundInvoke
    public Object aroundInvoke(InvocationContext invocationContext) throws Exception {
        final TransactionalAddon transactionalAddon = invocationContext.getMethod().getAnnotation(TransactionalAddon.class);
        if (transactionalAddon != null) {
            transactionalAddonContext.setReadOnly(transactionalAddon.readOnly());
        }
        return invocationContext.proceed();
    }
}