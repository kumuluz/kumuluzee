package com.kumuluz.ee.jaxws.cxf.impl;

import com.kumuluz.ee.jaxws.cxf.annotations.WsContext;

import javax.jws.WebService;

@WsContext(contextRoot = WsContextAnnotatedEndpointBean.CTX_ROOT, urlPattern = WsContextAnnotatedEndpointBean.URL_PATTERN)
@WebService(wsdlLocation = WsContextAnnotatedEndpointBean.WSDL_URL, portName = WsContextAnnotatedEndpointBean.PORT, serviceName =
        WsContextAnnotatedEndpointBean.SERVICE, endpointInterface = WsContextAnnotatedEndpointBean.INTERFACE)
public class WsContextAnnotatedEndpointBean implements WsContextAnnotatedEndpoint {

    public static final String WSDL_URL = "http://cloud.si/example.wsdl";
    public static final String CTX_ROOT = "/ws/*";
    public static final String URL_PATTERN = "/endpoint";
    public static final String PORT = "EndpointPort";
    public static final String SERVICE = "ServiceName";
    public static final String INTERFACE = "com.kumuluz.ee.jaxws.cxf.impl.WsContextAnnotatedEndpoint";

}
