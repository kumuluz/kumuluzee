/*
 *  Copyright (c) 2014-2018 Kumuluz and/or its affiliates
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
package com.kumuluz.ee.jaxws.cxf.ws;

import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxws.JAXWSMethodInvoker;
import org.apache.cxf.jaxws.context.WebServiceContextImpl;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.invoker.Factory;
import org.apache.cxf.service.invoker.Invoker;
import org.apache.cxf.service.invoker.MethodDispatcher;
import org.apache.cxf.service.model.BindingOperationInfo;

import javax.xml.ws.WebServiceContext;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author gpor89
 * @since 2.6.0
 */
public class KumuluzWSInvoker extends JAXWSMethodInvoker implements Invoker {

    private Class<?> targetClass;
    private Object targetBean;

    public KumuluzWSInvoker(final Class<?> targetClass, final Object targetBean) {
        super((Factory) null);
        this.targetClass = targetClass;
        this.targetBean = targetBean;
    }

    @Override
    public Object invoke(Exchange exchange, Object o) {
        BindingOperationInfo bop = exchange.get(BindingOperationInfo.class);
        MethodDispatcher md = (MethodDispatcher) exchange.get(Service.class).get(MethodDispatcher.class.getName());
        List<Object> params = null;
        if (o instanceof List) {
            params = CastUtils.cast((List<?>) o);
        } else if (o != null) {
            params = new MessageContentsList(o);
        }

        final Method method = adjustMethodAndParams(md.getMethod(bop), exchange, params, targetClass);

        return invoke(exchange, targetBean, method, params);
    }


    @Override
    protected Object performInvocation(Exchange exchange, final Object serviceObject, Method m, Object[] paramArray) throws Exception {
        WebServiceContext wsContext = new WebServiceContextImpl(null);

        try {
            KumuluzWebServiceContext.getInstance().setMessageContext(wsContext);

            return super.performInvocation(exchange, serviceObject, m, paramArray);
        } finally {
            KumuluzWebServiceContext.getInstance().setMessageContext(null);
        }
    }

}
