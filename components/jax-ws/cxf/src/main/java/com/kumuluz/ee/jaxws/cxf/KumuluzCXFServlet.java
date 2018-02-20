package com.kumuluz.ee.jaxws.cxf;

import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletConfig;
import javax.xml.namespace.QName;

public class KumuluzCXFServlet extends CXFNonSpringServlet {

    @Override
    protected void loadBus(ServletConfig sc) {
        super.loadBus(sc);

        //todo read this from xml config file
        final String path = "/soap";
        final String implementator = "https.github_com.gpor89.soap.example.Calculator";
        final String wsdlLocation = "/webapp/WEB-INF/wsdls/calculatorSample.wsdl";

        //start init
        JaxWsServerFactoryBean fb = new JaxWsServerFactoryBean();
        fb.setWsdlLocation(wsdlLocation);
        fb.setAddress(path);
        fb.setBus(BusFactory.getThreadDefaultBus());

        Object instance = getBean(implementator);
        fb.setServiceBean(instance);
        fb.setServiceName(new QName("https://github.com/gpor89/soap/example", "Calculator"));

        fb.create();
    }

    private Object getBean(String beanName) {
        CDI<Object> cdi = CDI.current();
        try {
            return cdi.select(Class.forName(beanName)).get();
        } catch (ClassNotFoundException e) {
            //todo error handling...
            e.printStackTrace();
            return null;
        }
    }
}
