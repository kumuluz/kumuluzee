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
package com.kumuluz.ee.common.datasources;

import com.kumuluz.ee.common.config.XaDataSourceConfig;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;

import javax.sql.XADataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public class XADataSourceBuilder {

    private XaDataSourceConfig xaDataSourceConfig;

    public XADataSourceBuilder(XaDataSourceConfig xaDataSourceConfig) {
        this.xaDataSourceConfig = xaDataSourceConfig;
    }

    public XADataSource constructXaDataSource() {

        if (xaDataSourceConfig == null) {

            throw new KumuluzServerException("The XADataSource configuration object for creating a XADataSource cannot be null.");
        }

        if (xaDataSourceConfig.getXaDatasourceClass() == null) {

            throw new KumuluzServerException("The XADataSource class config property 'xa-datasource-class' for creating a XADataSource must be provided.");
        }

        XADataSource xaDataSource = createXaDataSourceObject();

        setProperty(xaDataSource, "user", xaDataSourceConfig.getUsername());
        setProperty(xaDataSource, "password", xaDataSourceConfig.getPassword());
        setProperty(xaDataSource, "serverName", xaDataSourceConfig.getServerName());
        setProperty(xaDataSource, "portNumber", xaDataSourceConfig.getPortNumber());
        setProperty(xaDataSource, "databaseName", xaDataSourceConfig.getDatabaseName());

        return xaDataSource;
    }

    private XADataSource createXaDataSourceObject() {

        try {
            return (XADataSource) Class.forName(xaDataSourceConfig.getXaDatasourceClass()).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {

            String msg = "The XADataSource class '" + xaDataSourceConfig.getXaDatasourceClass() +
                    "' is either incorrect or is not present in the classpath.";

            throw new KumuluzServerException(msg, e);
        }
    }

    private void setProperty(Object object, String name, String value) {

        name = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);

        Method[] methods = object.getClass().getMethods();

        Method matchingMethod = null;

        for (Method method : methods) {

            if (method.getName().equals(name) && method.getParameterTypes().length == 1) {
                // ignores overloading, just takes the first match it finds.
                matchingMethod = method;
                break;
            }
        }

        if (matchingMethod == null) {

            String msg = "The XADataSource configuration property '" + name + "' is either incorrect or doesn't exist.";

            throw new KumuluzServerException(msg);
        }

        Class type = matchingMethod.getParameterTypes()[0];
        Object argument = value;

        if (type == Integer.TYPE) {
            argument = Integer.valueOf(value);
        }
        if (type == Boolean.TYPE) {
            argument = Boolean.valueOf(value);
        }

        try {
            matchingMethod.invoke(object, argument);
        } catch (IllegalAccessException | InvocationTargetException e) {

            String msg = "The XADataSource configuration property '" + name + "' is either incorrect or doesn't exist.";

            throw new KumuluzServerException(msg, e);
        }
    }
}
