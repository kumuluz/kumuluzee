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
package com.kumuluz.ee.common;

import com.kumuluz.ee.common.servlet.ServletWrapper;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.sql.DataSource;
import java.util.*;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public interface ServletServer extends KumuluzServer {

    void registerServlet(Class<? extends Servlet> servletClass, String mapping);

    void registerServlet(Class<? extends Servlet> servletClass, String mapping, Map<String, String> parameters);

    void registerServlet(Class<? extends Servlet> servletClass, String mapping, Map<String, String> parameters, int initOrder);

    void registerListener(EventListener listener);

    void registerFilter(Class<? extends Filter> filterClass, String pathSpec);

    void registerFilter(Class<? extends Filter> filterClass, String pathSpec, Map<String, String> parameters);

    void registerFilter(Class<? extends Filter> filterClass, String pathSpec, EnumSet<DispatcherType> dispatches);

    void registerFilter(Class<? extends Filter> filterClass, String pathSpec, EnumSet<DispatcherType> dispatches, Map<String, String> parameters);

    void registerResource(Object o, String jndiName);

    void registerDataSource(DataSource ds, String jndiName);

    void initWebContext();

    List<ServletWrapper> getRegisteredServlets();
}
