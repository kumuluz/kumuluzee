package com.kumuluz.ee.common.dependencies;

/**
 * @author Tilen Faganel
 * @since 2.0.0
 */
public enum EeComponentType {

    SERVLET(""),

    WEBSOCKET(""),

    JSP(""),

    EL(""),

    JSF(""),

    JPA(""),

    CDI(""),

    JAX_RS(""),

    JAX_WS(""),

    BEAN_VALIDATION(""),

    JSON_P(""),

    JTA(""),

    BATCH(""),

    MAIL("");

    private final String name;

    EeComponentType(String name) {
        this.name = name;
    }

}
