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
package com.kumuluz.ee.jaxws.cxf.processor;

import com.kumuluz.ee.jaxws.cxf.annotations.WsContext;
import com.kumuluz.ee.jaxws.cxf.ws.Endpoint;
import jakarta.jws.WebService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author gpor89
 * @since 3.0.0
 */
public class JaxWsAnnotationProcessorUtil {

    private static final Logger LOG = Logger.getLogger(JaxWsAnnotationProcessorUtil.class.getName());

    private static final JaxWsAnnotationProcessorUtil INSTANCE = new JaxWsAnnotationProcessorUtil();

    private static final String DEFAULT_CTX_ROOT = "/*";

    private boolean initialized;
    private List<Endpoint> endpointList;
    private String contextRoot;

    private JaxWsAnnotationProcessorUtil() {
    }

    public static synchronized JaxWsAnnotationProcessorUtil getInstance() {

        if (!INSTANCE.isInitialized()) {
            INSTANCE.init();
        }

        return INSTANCE;
    }

    private void init() {
        contextRoot = DEFAULT_CTX_ROOT;
        endpointList = new LinkedList<>();

        List<Class<?>> wsClasses = getWsClasses();
        for (Class<?> wsClass : wsClasses) {
            if (targetClassIsProxied(wsClass)) {
                wsClass = wsClass.getSuperclass();
            }

            final WebService wsAnnotation = wsClass.getAnnotation(WebService.class);

            if (wsAnnotation == null) {
                continue;
            }

            WsContext wsContext = wsClass.getAnnotation(WsContext.class);

            String url = null;
            if (wsContext != null) {
                String annCtx = wsContext.contextRoot();
                url = wsContext.urlPattern();

                setContextRoot(annCtx);
            }

            endpointList.add(new Endpoint(wsClass, url, wsAnnotation));
        }

        initialized = true;
    }

    private List<Class<?>> getWsClasses() {
        List<Class<?>> wsClasses = new ArrayList<>();

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream("META-INF/ws/java.lang.Object");

        if (is != null) {
            Scanner scanner = new Scanner(is);

            while (scanner.hasNextLine()) {
                String className = scanner.nextLine();
                try {
                    Class resourceClass = Class.forName(className);
                    wsClasses.add(resourceClass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            scanner.close();

        }

        return wsClasses;
    }

    private boolean targetClassIsProxied(Class targetClass) {
        return targetClass.getCanonicalName().contains("$Proxy");
    }

    private boolean isInitialized() {
        return initialized;
    }

    public String getContextRoot() {
        return contextRoot;
    }

    public void setContextRoot(String contextRoot) {
        if (contextRoot != null) {
            if (!contextRoot.startsWith("/")) {
                contextRoot = "/" + contextRoot;
            }

            if (!contextRoot.endsWith("/*")) {
                contextRoot = contextRoot.endsWith("/") ? contextRoot + "*" : contextRoot + "/*";
            }

            this.contextRoot = contextRoot;
        }
    }

    public List<Endpoint> getEndpointList() {
        return endpointList;
    }

    protected void reinitialize() {
        init();
    }
}

