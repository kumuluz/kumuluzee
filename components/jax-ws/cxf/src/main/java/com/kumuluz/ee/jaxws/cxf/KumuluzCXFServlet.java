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
package com.kumuluz.ee.jaxws.cxf;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.jaxws.cxf.config.Endpoint;
import com.kumuluz.ee.jaxws.cxf.ws.InjectionHelper;
import com.kumuluz.ee.jaxws.cxf.ws.KumuluzWSInvoker;
import com.kumuluz.ee.jaxws.cxf.ws.KumuluzWebServiceContext;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.service.invoker.Invoker;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.annotation.Resource;
import javax.enterprise.inject.spi.CDI;
import javax.jws.WebService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.ws.WebServiceContext;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author gpor89
 * @since 2.6.0
 */
public class KumuluzCXFServlet extends CXFNonSpringServlet {

    protected static final String JNDI_NAME_PREFIX = "java:comp/env/";
    protected static final String CDI_INIT_PARAM = "useCdi";
    protected static final String CONTEXT_ROOT = "contextRoot";

    private static final Logger LOG = Logger.getLogger(KumuluzCXFServlet.class.getSimpleName());

    private List<Endpoint> endpoints;

    public void init() throws ServletException {
        super.init();
        String contextPath = getInitParameter(CONTEXT_ROOT);
        this.endpoints = Endpoint.readEndpointList(ConfigurationUtil.getInstance(), contextPath);
    }

    @Override
    protected void loadBus(ServletConfig sc) {
        super.loadBus(sc);

        endpoints.stream().forEach(
                endpoint -> {
                    final JaxWsServerFactoryBean fb = new JaxWsServerFactoryBean();
                    fb.setBlockPostConstruct(true);

                    fb.setAddress(endpoint.getPath());
                    fb.setBus(this.bus);

                    final Class<?> implementationClass = endpoint.getImplementationClass();

                    final WebService wsAnnotation = implementationClass.getAnnotation(WebService.class);

                    if (wsAnnotation == null) {
                        return;
                    }

                    if (wsAnnotation.wsdlLocation() != null && !wsAnnotation.wsdlLocation().isEmpty()) {
                        fb.setWsdlLocation(wsAnnotation.wsdlLocation());
                    }

                    final Context context;
                    try {
                        context = new InitialContext();

                        try {
                            Stream.of(implementationClass.getDeclaredFields())
                                    .filter(f -> f.getType() == WebServiceContext.class)
                                    .forEach(f -> {
                                        Resource annotation = f.getAnnotation(Resource.class);

                                        if (annotation != null) {
                                            String name = annotation.name();
                                            String fieldName = f.getName();

                                            String bindName = null;
                                            try {

                                                if (name != null && !name.isEmpty()) {
                                                    bindName = JNDI_NAME_PREFIX + name;
                                                } else {
                                                    context.createSubcontext(JNDI_NAME_PREFIX + implementationClass.getName());
                                                    bindName = JNDI_NAME_PREFIX + implementationClass.getName() + "/" + fieldName;
                                                }

                                                context.bind(bindName, KumuluzWebServiceContext.getInstance());
                                            } catch (NameAlreadyBoundException e) {

                                            } catch (NamingException e) {
                                                throw new RuntimeException("Unable to register jndi context for " + bindName, e);
                                            }
                                        }
                                    });

                        } finally {
                            context.close();
                        }
                    } catch (NamingException e) {
                        throw new RuntimeException("Unable to register jndi context", e);
                    }

                    final Object targetBean = getBean(implementationClass);

                    InjectionHelper.injectWebServiceContext(targetBean, KumuluzWebServiceContext.getInstance());

                    //fb.setServiceBean(targetBean);
                    fb.setServiceClass(implementationClass);

                    Invoker i = new KumuluzWSInvoker(implementationClass, targetBean);
                    fb.setInvoker(i);

                    fb.create();

                    LOG.info("Webservice endpoint published with address=" + fb.getAddress() +
                            ", wsdlLocation=" + fb.getWsdlURL() +
                            ", implementor=" + implementationClass.getName() +
                            ", serviceName=" + wsAnnotation.serviceName() +
                            ", portName=" + wsAnnotation.portName());
                }
        );
    }

    private Object getBean(Class<?> clazz) {

        String useCdi = getInitParameter(CDI_INIT_PARAM);

        //cdi
        if ("true".equalsIgnoreCase(useCdi)) {
            return CDI.current().select(clazz).get();
        }

        //pojo instance
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to instantiate bean from " + clazz);
        }

    }
}
