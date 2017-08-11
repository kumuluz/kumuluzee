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

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Wrapper;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
@Deprecated
public class NonJtaXADataSourceWrapper implements XADataSourceWrapper {

    protected XADataSource xaDataSource;

    public NonJtaXADataSourceWrapper(XADataSource xaDataSource) {
        this.xaDataSource = xaDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {

        checkIfValid();

        XAConnection xaConnection = xaDataSource.getXAConnection();

        return new NonJtaXAConnectionWrapper(xaConnection);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {

        checkIfValid();

        XAConnection xaConnection = xaDataSource.getXAConnection(username, password);

        return new NonJtaXAConnectionWrapper(xaConnection);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {

        if (xaDataSource == null) {
            throw new SQLException("The underlying XADataSource is invalid or cannot be found");
        } else if (iface.isInstance(xaDataSource)) {
            return iface.cast(xaDataSource);
        } else if (xaDataSource instanceof Wrapper) {
            return ((java.sql.Wrapper) xaDataSource).unwrap(iface);
        } else {
            throw new SQLException("The requested interface cannot be unwrapped");
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {

        if (xaDataSource == null) {
            throw new SQLException("The underlying XADataSource is invalid or cannot be found");
        } else if (iface.isInstance(xaDataSource)) {
            return true;
        } else if (xaDataSource instanceof Wrapper) {
            return ((java.sql.Wrapper) xaDataSource).isWrapperFor(iface);
        }

        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {

        checkIfValid();

        return xaDataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

        checkIfValid();

        xaDataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

        checkIfValid();

        xaDataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {

        checkIfValid();

        return xaDataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {

        checkIfValid();

        return xaDataSource.getParentLogger();
    }

    protected void checkIfValid() {

        if (xaDataSource == null) throw new IllegalStateException("The XADataSource is invalid or cannot be found");
    }
}
