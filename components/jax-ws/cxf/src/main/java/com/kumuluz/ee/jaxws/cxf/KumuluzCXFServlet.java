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

import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletConfig;
import javax.xml.namespace.QName;

/**
 * @author gpor89
 * @since 2.6.0
 */
public class KumuluzCXFServlet extends CXFNonSpringServlet {

    @Override
    protected void loadBus(ServletConfig sc) {
        super.loadBus(sc);

        //todo read this from xml config file
        final String path = "/soap";
        final String implementator = "com.kumuluz.ee.cxf.sample.CalculatorSoapServiceBean";
        final String wsdlLocation = "/webapp/WEB-INF/wsdls/calculatorSample.wsdl";

        //start init
        JaxWsServerFactoryBean fb = new JaxWsServerFactoryBean();
        fb.setWsdlLocation(wsdlLocation);
        fb.setAddress(path);
        fb.setBus(BusFactory.getThreadDefaultBus());

        Object instance = getBean(implementator);
        fb.setServiceBean(instance);
        fb.setServiceName(new QName("https://github.com/gpor89/soap/sample", "Calculator"));

        fb.create();
    }

    private Object getBean(String className) {

        String useCdi = getInitParameter("useCdi");

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
