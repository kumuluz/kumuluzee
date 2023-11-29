package com.kumuluz.ee.jaxws.cxf.processor;

import com.kumuluz.ee.jaxws.cxf.impl.NoWsContextAnnotatedEndpointBean;
import com.kumuluz.ee.jaxws.cxf.impl.WsContextAnnotatedEndpointBean;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JaxWsAnnotationProcessorUtilTest {

    private final JaxWsAnnotationProcessorUtil instance = JaxWsAnnotationProcessorUtil.getInstance();

    @Test
    void shouldReturnDefaultWsInfo() {

        String className = NoWsContextAnnotatedEndpointBean.class.getName();
        Class<?> clazz = NoWsContextAnnotatedEndpointBean.class;

        prepareEndpointList(className);
        instance.reinitialize();

        String expectedContextRoot = "/*";
        String expectedUrl = "/" + className;

        //check implementation class
        assertEquals(clazz, instance.getEndpointList().get(0).getImplementationClass(),
                "Ws endpoint returned wrong implementation value");

        //assert @WsContext
        assertEquals(expectedContextRoot, instance.getContextRoot(),
                "Ws endpoint without defined contextRoot returned wrong value");
        assertFalse(instance.getEndpointList().isEmpty());
        assertEquals(expectedUrl, instance.getEndpointList().get(0).getUrl(),
                "Ws endpoint without defined url returned wrong value");

        //assert @Webservice
        assertNull(instance.getEndpointList().get(0).wsdlLocation(),
                "Ws endpoint without defined wsdlLocation returned wrong value");
        assertNull(instance.getEndpointList().get(0).portName(),
                "Ws endpoint without defined wsdlLocation returned wrong value");
        assertNull(instance.getEndpointList().get(0).serviceName(),
                "Ws endpoint without defined wsdlLocation returned wrong value");
    }

    @Test
    void shouldReturnConfiguredWsInfo() {

        String className = WsContextAnnotatedEndpointBean.class.getName();
        Class<?> clazz = WsContextAnnotatedEndpointBean.class;

        prepareEndpointList(className);
        instance.reinitialize();


        //check implementation class
        assertEquals(clazz, instance.getEndpointList().get(0).getImplementationClass(),
                "Ws endpoint returned wrong implementation value");

        //assert @WsContext
        assertEquals(WsContextAnnotatedEndpointBean.CTX_ROOT, instance.getContextRoot(),
                "Ws endpoint without defined contextRoot returned wrong value");
        assertFalse(instance.getEndpointList().isEmpty());
        assertEquals(WsContextAnnotatedEndpointBean.URL_PATTERN, instance.getEndpointList().get(0).getUrl(),
                "Ws endpoint without defined url returned wrong value");

        //assert @Webservice
        assertEquals(WsContextAnnotatedEndpointBean.WSDL_URL, instance.getEndpointList().get(0).wsdlLocation(),
                "Ws endpoint without defined wsdlLocation returned wrong value");
        assertEquals(WsContextAnnotatedEndpointBean.PORT, instance.getEndpointList().get(0).portName(),
                "Ws endpoint without defined wsdlLocation returned wrong value");
        assertEquals(WsContextAnnotatedEndpointBean.SERVICE, instance.getEndpointList().get(0).serviceName(),
                "Ws endpoint without defined wsdlLocation returned wrong value");
    }

    //helper method
    private void prepareEndpointList(String... endpoints) {

        URL url = this.getClass().getClassLoader().getResource("META-INF/ws/java.lang.Object");
        if (url == null) {
            throw new RuntimeException("Service file not found.");
        }

        try (PrintWriter out = new PrintWriter(URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8))) {
            Stream.of(endpoints).forEach(out::print);
            out.flush();
        } catch (IOException fe) {
            fe.printStackTrace();
        }
    }

}
