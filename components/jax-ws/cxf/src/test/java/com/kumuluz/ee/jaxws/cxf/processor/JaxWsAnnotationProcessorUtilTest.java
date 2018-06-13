package com.kumuluz.ee.jaxws.cxf.processor;

import com.kumuluz.ee.jaxws.cxf.impl.NoWsContextAnnotatedEndpointBean;
import com.kumuluz.ee.jaxws.cxf.impl.WsContextAnnotatedEndpointBean;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class JaxWsAnnotationProcessorUtilTest {

    private JaxWsAnnotationProcessorUtil instance = JaxWsAnnotationProcessorUtil.getInstance();

    @Test
    public void shouldReturnDefaultWsInfo() {

        String className = NoWsContextAnnotatedEndpointBean.class.getName();
        Class<?> clazz = NoWsContextAnnotatedEndpointBean.class;

        prepareEndpointList(className);
        instance.reinitialize();

        String expectedContextRoot = "/*";
        String expectedUrl = "/" + className;

        //check implementation class
        assertEquals("Ws endpoint returned wrong implementation value", clazz, instance.getEndpointList().get(0).getImplementationClass());

        //assert @WsContext
        assertEquals("Ws endpoint without defined contextRoot returned wrong value", expectedContextRoot, instance.getContextRoot());
        assertFalse(instance.getEndpointList().isEmpty());
        assertEquals("Ws endpoint without defined url returned wrong value", expectedUrl, instance.getEndpointList().get(0).getUrl());

        //assert @Webservice
        assertNull("Ws endpoint without defined wsdlLocation returned wrong value", instance.getEndpointList().get(0).wsdlLocation());
        assertNull("Ws endpoint without defined wsdlLocation returned wrong value", instance.getEndpointList().get(0).portName());
        assertNull("Ws endpoint without defined wsdlLocation returned wrong value", instance.getEndpointList().get(0).serviceName());

    }

    @Test
    public void shouldReturnConfiguredWsInfo() {

        String className = WsContextAnnotatedEndpointBean.class.getName();
        Class<?> clazz = WsContextAnnotatedEndpointBean.class;

        prepareEndpointList(className);
        instance.reinitialize();


        //check implementation class
        assertEquals("Ws endpoint returned wrong implementation value", clazz, instance.getEndpointList().get(0).getImplementationClass());

        //assert @WsContext
        assertEquals("Ws endpoint without defined contextRoot returned wrong value", WsContextAnnotatedEndpointBean.CTX_ROOT, instance.getContextRoot());
        assertFalse(instance.getEndpointList().isEmpty());
        assertEquals("Ws endpoint without defined url returned wrong value", WsContextAnnotatedEndpointBean.URL_PATTERN, instance.getEndpointList().get(0)
                .getUrl());

        //assert @Webservice
        assertEquals("Ws endpoint without defined wsdlLocation returned wrong value", WsContextAnnotatedEndpointBean.WSDL_URL, instance.getEndpointList()
                .get(0).wsdlLocation());
        assertEquals("Ws endpoint without defined wsdlLocation returned wrong value", WsContextAnnotatedEndpointBean.PORT, instance.getEndpointList()
                .get(0).portName());
        assertEquals("Ws endpoint without defined wsdlLocation returned wrong value", WsContextAnnotatedEndpointBean.SERVICE, instance.getEndpointList()
                .get(0).serviceName());
    }

    //helper method
    private void prepareEndpointList(String... endpoints) {

        URL url = this.getClass().getClassLoader().getResource("META-INF/ws/java.lang.Object");
        if (url == null) {
            throw new RuntimeException("Service file not found.");
        }

        try (PrintWriter out = new PrintWriter(new File(URLDecoder.decode(url.getFile(), "UTF-8")))) {
            Stream.of(endpoints).forEach(out::print);
            out.flush();
        } catch (IOException fe) {
            fe.printStackTrace();
        }
    }

}
