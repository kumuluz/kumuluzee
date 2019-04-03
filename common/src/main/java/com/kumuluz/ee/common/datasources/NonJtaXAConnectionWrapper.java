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

import javax.sql.XAConnection;
import java.sql.*;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
@Deprecated
public class NonJtaXAConnectionWrapper implements Connection {

    private Boolean isClosed = false;

    private XAConnection xaConnection;

    public NonJtaXAConnectionWrapper(XAConnection xaConnection) {
        this.xaConnection = xaConnection;
    }

    @Override
    public Statement createStatement() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

        checkIfValid();

        xaConnection.getConnection().setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {

        checkIfValid();

        xaConnection.getConnection().commit();
    }

    @Override
    public void rollback() throws SQLException {

        checkIfValid();

        xaConnection.getConnection().rollback();
    }

    @Override
    public void close() throws SQLException {

        if (isClosed) return;

        isClosed = true;

        if (xaConnection != null) {

            xaConnection.close();
            xaConnection = null;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {

        return isClosed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

        checkIfValid();

        xaConnection.getConnection().setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {

        checkIfValid();

        xaConnection.getConnection().setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {

        checkIfValid();

        xaConnection.getConnection().setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {

        checkIfValid();

        xaConnection.getConnection().clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

        checkIfValid();

        xaConnection.getConnection().setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

        checkIfValid();

        xaConnection.getConnection().setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

        checkIfValid();

        xaConnection.getConnection().rollback();
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

        checkIfValid();

        xaConnection.getConnection().releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

        try {
            checkIfValid();

            xaConnection.getConnection().setClientInfo(name, value);
        } catch (SQLException e) {
            throw new SQLClientInfoException(Collections.emptyMap(), e);
        }
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

        try {
            checkIfValid();

            xaConnection.getConnection().setClientInfo(properties);
        } catch (SQLException e) {
            throw new SQLClientInfoException(Collections.emptyMap(), e);
        }
    }

    @Override
    public String getClientInfo(String name) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {

        checkIfValid();

        xaConnection.getConnection().setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {

        checkIfValid();

        xaConnection.getConnection().abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

        checkIfValid();

        xaConnection.getConnection().setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().isWrapperFor(iface);
    }

    private void checkIfValid() throws SQLException {

        if (isClosed) throw new SQLException("The connection is closed");
        if (xaConnection == null) throw new SQLException("The connection is invalid");
    }
}
