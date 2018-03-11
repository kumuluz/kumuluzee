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
package com.kumuluz.ee.jaxws.cxf.ws;

import org.w3c.dom.Element;

import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.io.Serializable;
import java.security.Principal;

/**
 * @author gpor89
 * @since 2.6.0
 */
public final class KumuluzWebServiceContext implements WebServiceContext, Serializable {

    private static final long serialVersionUID = 6874657983243689841L;

    private static final KumuluzWebServiceContext WS_CONTEXT_SINGLETON = new KumuluzWebServiceContext();

    private final transient ThreadLocal<WebServiceContext> contexts = new InheritableThreadLocal<>();

    public static KumuluzWebServiceContext getInstance() {
        return WS_CONTEXT_SINGLETON;
    }

    public EndpointReference getEndpointReference(final Element... referenceParameters) {
        return getWebServiceContext().getEndpointReference(referenceParameters);
    }

    public <T extends EndpointReference> T getEndpointReference(final Class<T> clazz, final Element... referenceParameters) {
        return getWebServiceContext().getEndpointReference(clazz, referenceParameters);
    }

    public MessageContext getMessageContext() {
        return getWebServiceContext().getMessageContext();
    }

    public void setMessageContext(final WebServiceContext ctx) {
        this.contexts.set(ctx);
    }

    public Principal getUserPrincipal() {
        return getWebServiceContext().getUserPrincipal();
    }

    public boolean isUserInRole(String role) {
        return getWebServiceContext().isUserInRole(role);
    }

    private WebServiceContext getWebServiceContext() {
        final WebServiceContext delegate = contexts.get();

        if (delegate == null) {
            throw new IllegalStateException();
        }

        return delegate;
    }

}
