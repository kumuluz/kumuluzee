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

import com.kumuluz.ee.jta.common.JtaTransactionHolder;
import com.kumuluz.ee.jta.common.utils.TxUtils;

import javax.sql.XAConnection;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import java.sql.*;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public class JtaXAConnectionWrapper implements Connection {

    private Logger log = Logger.getLogger(JtaXAConnectionWrapper.class.getSimpleName());

    private Boolean isClosed = false;
    private Boolean isEnlistedInJTA = false;
    private Boolean closeAfterTransactionEnd = false;

    private XAConnection xaConnection;

    private TransactionManager transactionManager;

    public JtaXAConnectionWrapper(XAConnection xaConnection) {
        this.xaConnection = xaConnection;
    }

    @Override
    public Statement createStatement() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        Statement statement = xaConnection.getConnection().createStatement();

        return (Statement) XAStatementProxy.newInstance(statement, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        PreparedStatement preparedStatement = xaConnection.getConnection().prepareStatement(sql);

        return (PreparedStatement) XAStatementProxy.newInstance(preparedStatement, this);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        CallableStatement callableStatement = xaConnection.getConnection().prepareCall(sql);

        return (CallableStatement) XAStatementProxy.newInstance(callableStatement, this);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {

        checkIfValid();

        return xaConnection.getConnection().nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.getConnection().setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

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
        jtaPreInvoke();

        xaConnection.getConnection().rollback();
    }

    @Override
    public void close() throws SQLException {

        if (isClosed) return;

        isClosed = true;

        if (isEnlistedInJTA) {

            closeAfterTransactionEnd = true;
            return;
        }

        if (xaConnection != null) {

            xaConnection.close();
            xaConnection = null;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {

        if (!isClosed) {
            jtaPreInvoke();
        }

        return isClosed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.getConnection().setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.getConnection().setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.getConnection().setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.getConnection().clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        Statement statement = xaConnection.getConnection().createStatement(resultSetType, resultSetConcurrency);

        return (Statement) XAStatementProxy.newInstance(statement, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        PreparedStatement preparedStatement = xaConnection.getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);

        return (PreparedStatement) XAStatementProxy.newInstance(preparedStatement, this);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        CallableStatement callableStatement = xaConnection.getConnection().prepareCall(sql, resultSetType, resultSetConcurrency);

        return (CallableStatement) XAStatementProxy.newInstance(callableStatement, this);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.getConnection().setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.getConnection().setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.getConnection().rollback();
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.getConnection().releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        Statement statement = xaConnection.getConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);

        return (Statement) XAStatementProxy.newInstance(statement, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        PreparedStatement preparedStatement = xaConnection.getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);

        return (PreparedStatement) XAStatementProxy.newInstance(preparedStatement, this);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        CallableStatement callableStatement = xaConnection.getConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);

        return (CallableStatement) XAStatementProxy.newInstance(callableStatement, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        PreparedStatement preparedStatement = xaConnection.getConnection().prepareStatement(sql, autoGeneratedKeys);

        return (PreparedStatement) XAStatementProxy.newInstance(preparedStatement, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        PreparedStatement preparedStatement = xaConnection.getConnection().prepareStatement(sql, columnIndexes);

        return (PreparedStatement) XAStatementProxy.newInstance(preparedStatement, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        PreparedStatement preparedStatement = xaConnection.getConnection().prepareStatement(sql, columnNames);

        return (PreparedStatement) XAStatementProxy.newInstance(preparedStatement, this);
    }

    @Override
    public Clob createClob() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

        try {
            checkIfValid();
            jtaPreInvoke();

            xaConnection.getConnection().setClientInfo(name, value);
        } catch (SQLException e) {
            throw new SQLClientInfoException(Collections.emptyMap(), e);
        }
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

        try {
            checkIfValid();
            jtaPreInvoke();

            xaConnection.getConnection().setClientInfo(properties);
        } catch (SQLException e) {
            throw new SQLClientInfoException(Collections.emptyMap(), e);
        }
    }

    @Override
    public String getClientInfo(String name) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.getConnection().setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.getConnection().abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.getConnection().setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getConnection().isWrapperFor(iface);
    }

    private void checkIfValid() throws SQLException {

        if (isClosed) throw new SQLException("The connection is closed");
        if (xaConnection == null) throw new SQLException("The connection is invalid");
    }

    private void jtaPreInvoke() throws SQLException {

        if (isEnlistedInJTA) return;

        try {

            if (transactionManager == null) {
                transactionManager = JtaTransactionHolder.getInstance().getTransactionManager();
            }

            if (TxUtils.isActive(transactionManager)) {

                transactionManager.getTransaction().enlistResource(xaConnection.getXAResource());
                transactionManager.getTransaction().registerSynchronization(new Synchronization() {

                    @Override
                    public void beforeCompletion() {
                    }

                    @Override
                    public void afterCompletion(int status) {

                        JtaXAConnectionWrapper jtaXaConnectionWrapper = JtaXAConnectionWrapper.this;

                        jtaXaConnectionWrapper.isEnlistedInJTA = false;

                        if (jtaXaConnectionWrapper.closeAfterTransactionEnd && jtaXaConnectionWrapper.xaConnection != null) {

                            try {
                                jtaXaConnectionWrapper.xaConnection.close();
                                jtaXaConnectionWrapper.xaConnection = null;
                            } catch (SQLException e) {

                                log.severe("There was an error closing the connection after transaction complete");
                            }
                        }
                    }
                });

                this.isEnlistedInJTA = true;
            }
        } catch (SystemException | RollbackException | SQLException e) {
            throw new SQLException(e);
        }
    }
}
