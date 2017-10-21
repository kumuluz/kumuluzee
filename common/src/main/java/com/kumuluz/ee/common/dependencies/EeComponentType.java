/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
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

    EJB("EJB"),

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
