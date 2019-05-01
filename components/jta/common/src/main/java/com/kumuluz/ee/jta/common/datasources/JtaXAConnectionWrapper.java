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

import com.kumuluz.ee.jta.common.JtaProvider;
import com.kumuluz.ee.jta.common.utils.TxUtils;

import javax.sql.XAConnection;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;
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
@Deprecated
public class JtaXAConnectionWrapper implements Connection {

    private Logger log = Logger.getLogger(JtaXAConnectionWrapper.class.getSimpleName());

    private Boolean isClosed = false;
    private Boolean isEnlistedInJTA = false;
    private Boolean closeAfterTransactionEnd = false;

    private XAConnection parentConnection;
    private Connection xaConnection;
    private XAResource xaResource;

    private TransactionManager transactionManager;

    public JtaXAConnectionWrapper(XAConnection xaConnection) throws SQLException {
        this.xaResource = xaConnection.getXAResource();
        this.parentConnection = xaConnection;
        this.xaConnection = xaConnection.getConnection();
    }

    @Override
    public Statement createStatement() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        Statement statement = xaConnection.createStatement();

        return (Statement) XAStatementProxy.newInstance(statement, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        PreparedStatement preparedStatement = xaConnection.prepareStatement(sql);

        return (PreparedStatement) XAStatementProxy.newInstance(preparedStatement, this);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        CallableStatement callableStatement = xaConnection.prepareCall(sql);

        return (CallableStatement) XAStatementProxy.newInstance(callableStatement, this);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {

        checkIfValid();

        return xaConnection.nativeSQL(sql);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getAutoCommit();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.setAutoCommit(autoCommit);
    }

    @Override
    public void commit() throws SQLException {

        checkIfValid();

        xaConnection.commit();
    }

    @Override
    public void rollback() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.rollback();
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

        return xaConnection.getMetaData();
    }

    @Override
    public boolean isReadOnly() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.isReadOnly();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.setReadOnly(readOnly);
    }

    @Override
    public String getCatalog() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getCatalog();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.setCatalog(catalog);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getTransactionIsolation();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.setTransactionIsolation(level);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        Statement statement = xaConnection.createStatement(resultSetType, resultSetConcurrency);

        return (Statement) XAStatementProxy.newInstance(statement, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        PreparedStatement preparedStatement = xaConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);

        return (PreparedStatement) XAStatementProxy.newInstance(preparedStatement, this);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        CallableStatement callableStatement = xaConnection.prepareCall(sql, resultSetType, resultSetConcurrency);

        return (CallableStatement) XAStatementProxy.newInstance(callableStatement, this);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.setTypeMap(map);
    }

    @Override
    public int getHoldability() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getHoldability();
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.setHoldability(holdability);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.rollback();
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        Statement statement = xaConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);

        return (Statement) XAStatementProxy.newInstance(statement, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        PreparedStatement preparedStatement = xaConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);

        return (PreparedStatement) XAStatementProxy.newInstance(preparedStatement, this);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        CallableStatement callableStatement = xaConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);

        return (CallableStatement) XAStatementProxy.newInstance(callableStatement, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        PreparedStatement preparedStatement = xaConnection.prepareStatement(sql, autoGeneratedKeys);

        return (PreparedStatement) XAStatementProxy.newInstance(preparedStatement, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        PreparedStatement preparedStatement = xaConnection.prepareStatement(sql, columnIndexes);

        return (PreparedStatement) XAStatementProxy.newInstance(preparedStatement, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        PreparedStatement preparedStatement = xaConnection.prepareStatement(sql, columnNames);

        return (PreparedStatement) XAStatementProxy.newInstance(preparedStatement, this);
    }

    @Override
    public Clob createClob() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

        try {
            checkIfValid();
            jtaPreInvoke();

            xaConnection.setClientInfo(name, value);
        } catch (SQLException e) {
            throw new SQLClientInfoException(Collections.emptyMap(), e);
        }
    }

    @Override
    public String getClientInfo(String name) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getClientInfo();
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

        try {
            checkIfValid();
            jtaPreInvoke();

            xaConnection.setClientInfo(properties);
        } catch (SQLException e) {
            throw new SQLClientInfoException(Collections.emptyMap(), e);
        }
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.createStruct(typeName, attributes);
    }

    @Override
    public String getSchema() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getSchema();
    }

    @Override
    public void setSchema(String schema) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.setSchema(schema);
    }

    @Override
    public void abort(Executor executor) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        xaConnection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {

        checkIfValid();
        jtaPreInvoke();

        return xaConnection.isWrapperFor(iface);
    }

    private void checkIfValid() throws SQLException {

        if (isClosed) throw new SQLException("The connection is closed");
        if (xaConnection == null) throw new SQLException("The connection is invalid");
    }

    private void jtaPreInvoke() throws SQLException {

        if (isEnlistedInJTA) return;

        try {

            if (transactionManager == null) {
                transactionManager = JtaProvider.getInstance().getTransactionManager();
            }

            if (TxUtils.isActive(transactionManager)) {

                transactionManager.getTransaction().enlistResource(xaResource);
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
                                jtaXaConnectionWrapper.parentConnection.close();
                                jtaXaConnectionWrapper.parentConnection = null;
                            } catch (SQLException e) {

                                log.severe("There was an error closing the connection after transaction complete");
                            }
                        }
                    }
                });

                this.isEnlistedInJTA = true;
            }
        } catch (SystemException | RollbackException e) {
            throw new SQLException(e);
        }
    }
}
