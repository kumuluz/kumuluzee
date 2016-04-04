package com.kumuluz.ee.common.dependencies;

/**
 * @author Tilen Faganel
 * @since 2.0.0
 */
public enum EeComponentType {

    SERVLET("Servlet"),

    WEBSOCKET("WebSocket"),

    JSP("JSP"),

    EL("EL"),

    JSF("JSF"),

    JPA("JPA"),

    CDI("CDI"),

    JAX_RS("JAX-RS"),

    JAX_WS("JAX-WS"),

    BEAN_VALIDATION("Bean Validation"),

    JSON_P("JSON-P"),

    JTA("JTA"),

    BATCH("Batch"),

    MAIL("JavaMail");

    private final String name;

    EeComponentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
