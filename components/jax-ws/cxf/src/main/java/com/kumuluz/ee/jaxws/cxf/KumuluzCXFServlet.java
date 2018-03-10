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
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.List;

/**
 * @author gpor89
 * @since 2.6.0
 */
public class KumuluzCXFServlet extends CXFNonSpringServlet {

    protected static final String CDI_INIT_PARAM = "useCdi";

    private List<Endpoint> endpoints;

    public void init() throws ServletException {
        this.endpoints = Endpoint.readEndpointList(ConfigurationUtil.getInstance());
    }

    @Override
    protected void loadBus(ServletConfig sc) {
        super.loadBus(sc);

        endpoints.stream().forEach(
                endpoint -> {

                    final JaxWsServerFactoryBean fb = new JaxWsServerFactoryBean();

                    fb.setAddress(endpoint.getPath());
                    fb.setBus(BusFactory.getThreadDefaultBus());
                    if (endpoint.getWsdlLocation().isPresent()) {
                        fb.setWsdlLocation(endpoint.getWsdlLocation().get());
                    }

                    final Object instance = getBean(endpoint.getImplementationClass());
                    fb.setServiceBean(instance);

                    fb.create();
                }
        );
    }

    private Object getBean(String className) {

        String useCdi = getInitParameter(CDI_INIT_PARAM);

        if ("true".equalsIgnoreCase(useCdi)) {
            CDI<Object> cdi = CDI.current();
            try {
                return cdi.select(Class.forName(className)).get();
            } catch (ClassNotFoundException e) {
                //todo error handling...
                e.printStackTrace();
                return null;
            }
        }

        //pojo instance
        try {
            return Class.forName(className).getConstructor().newInstance();
        } catch (Exception e) {
            //todo error handling...
            e.printStackTrace();
            return null;
        }
    }
}
