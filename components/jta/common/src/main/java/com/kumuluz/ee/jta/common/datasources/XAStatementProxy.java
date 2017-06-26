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
package com.kumuluz.ee.jta.common.datasources;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public class XAStatementProxy implements InvocationHandler {

    private Statement statement;
    private JtaXAConnectionWrapper jtaXaConnectionWrapper;


    private XAStatementProxy(Statement statement, JtaXAConnectionWrapper jtaXaConnectionWrapper) {
        this.statement = statement;
        this.jtaXaConnectionWrapper = jtaXaConnectionWrapper;
    }

    public static Object newInstance(Statement statement, JtaXAConnectionWrapper jtaXaConnectionWrapper) {
        return Proxy.newProxyInstance(statement.getClass().getClassLoader(), new Class<?>[]{Statement.class}, new XAStatementProxy(statement, jtaXaConnectionWrapper));
    }

    public static Object newInstance(PreparedStatement statement, JtaXAConnectionWrapper jtaXaConnectionWrapper) {
        return Proxy.newProxyInstance(statement.getClass().getClassLoader(), new Class<?>[]{PreparedStatement.class}, new XAStatementProxy(statement, jtaXaConnectionWrapper));
    }

    public static Object newInstance(CallableStatement statement, JtaXAConnectionWrapper jtaXaConnectionWrapper) {
        return Proxy.newProxyInstance(statement.getClass().getClassLoader(), new Class<?>[]{CallableStatement.class}, new XAStatementProxy(statement, jtaXaConnectionWrapper));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (jtaXaConnectionWrapper.isClosed()) {
            throw new SQLException("The connection associated with this statement is closed");
        }

        return method.invoke(statement, args);
    }
}
