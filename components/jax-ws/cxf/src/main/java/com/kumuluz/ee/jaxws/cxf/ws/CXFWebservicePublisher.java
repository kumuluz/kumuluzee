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

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.jaxws.handler.AnnotationHandlerChainBuilder;
import org.apache.cxf.service.invoker.Invoker;

import javax.annotation.Resource;
import javax.enterprise.inject.spi.CDI;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.xml.ws.WebServiceContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author gpor89
 * @since 3.0.0
 */
public class CXFWebservicePublisher {

    protected static final String JNDI_NAME_PREFIX = "java:comp/env/";
    private static Logger LOG = Logger.getLogger(CXFWebservicePublisher.class.getSimpleName());

    private Context context;

    public Server publish(final Endpoint endpoint, final Bus bus, final boolean cdiPresent) {

        final Class<?> clazz = endpoint.getImplementationClass();
        final String url = endpoint.getUrl();

        //inject resources into WS instance
        Stream.of(clazz.getDeclaredFields())
                .filter(f -> f.getType() == WebServiceContext.class)
                .forEach(f -> injectWebServiceResource(f, clazz));

        final Object targetBean = getBean(clazz, cdiPresent);

        AnnotationHandlerChainBuilder builder = new AnnotationHandlerChainBuilder(bus);
        InjectionHelper.injectWebServiceContext(targetBean, KumuluzWebServiceContext.getInstance());

        final Invoker invoker = new KumuluzWSInvoker(clazz, targetBean);

        final JaxWsServerFactoryBean fb = new JaxWsServerFactoryBean();

        fb.setBlockPostConstruct(true);
        fb.setAddress(url);
        fb.setBus(bus);
        fb.setServiceClass(clazz);
        fb.setInvoker(invoker);
        fb.setHandlers(builder.buildHandlerChainFromClass(clazz, fb.getEndpointName(), fb.getServiceName(), fb.getBindingId()));

        if (endpoint.wsdlLocation() != null) {
            //top-down approach
            fb.setWsdlLocation(endpoint.wsdlLocation());
        }

        Server server = fb.create();

        LOG.info("Webservice endpoint published with address=" + fb.getAddress() +
                ", wsdlLocation=" + fb.getWsdlURL() +
                ", implementor=" + clazz.getName() +
                ", serviceName=" + endpoint.serviceName() +
                ", portName=" + endpoint.portName());

        return server;
    }

    protected void injectWebServiceResource(final Field f, final Class<?> clazz) {
        Resource annotation = f.getAnnotation(Resource.class);

        if (annotation != null) {
            String name = annotation.name();
            String fieldName = f.getName();

            if (context == null) {
                open();
            }

            String bindName = null;
            try {

                if (name != null && !name.isEmpty()) {
                    bindName = JNDI_NAME_PREFIX + name;
                } else {
                    context.createSubcontext(JNDI_NAME_PREFIX + clazz.getName());
                    bindName = JNDI_NAME_PREFIX + clazz.getName() + "/" + fieldName;
                }

                context.bind(bindName, KumuluzWebServiceContext.getInstance());
            } catch (NameAlreadyBoundException e) {

            } catch (NamingException e) {
                throw new RuntimeException("Unable to register jndi context for " + bindName, e);
            }
        }
    }


    private Object getBean(Class<?> clazz, boolean cdiPresent) {
        //cdi
        if (cdiPresent) {
            return CDI.current().select(clazz).get();
        }

        //pojo instance
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to instantiate bean from " + clazz);
        }

    }

    private void open() {
        if (context == null) {
            try {
                context = new InitialContext();
            } catch (NamingException e) {
                throw new RuntimeException("Unable to register jndi context", e);
            }
        }
    }

    public void close() {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException e) {
                LOG.severe("Cannot close context");
            }
        }
    }

}
