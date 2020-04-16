package com.kumuluz.ee.factories;

import com.kumuluz.ee.common.config.DataSourceConfig;
import com.kumuluz.ee.common.config.DataSourcePoolConfig;
import com.kumuluz.ee.common.config.XaDataSourceConfig;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;
import com.kumuluz.ee.common.utils.StringUtils;
import com.kumuluz.ee.jta.common.JtaProvider;
import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.AgroalConnectionFactoryConfiguration.TransactionIsolation;
import io.agroal.api.configuration.AgroalConnectionPoolConfiguration;
import io.agroal.api.configuration.AgroalDataSourceConfiguration.DataSourceImplementation;
import io.agroal.api.configuration.supplier.AgroalConnectionFactoryConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalConnectionPoolConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Marcos Koch Salvador
 * @since 3.7.0
 */
public class AgroalDataSourceFactory {

    private static final Logger log = Logger.getLogger(AgroalDataSourceFactory.class.getSimpleName());

    public static AgroalDataSource createDataSource(DataSourceConfig dsc, boolean jtaPresent) {
        AgroalDataSourceConfigurationSupplier dataSourceConfig = new AgroalDataSourceConfigurationSupplier();

        AgroalConnectionPoolConfigurationSupplier poolConfig = dataSourceConfig.connectionPoolConfiguration();

        AgroalConnectionFactoryConfigurationSupplier connectionFactoryConfig = poolConfig.connectionFactoryConfiguration();

        if (!jtaPresent) {
            dataSourceConfig.dataSourceImplementation(DataSourceImplementation.HIKARI);
        } else {
            poolConfig.transactionIntegration( JtaProvider.getInstance().getTransactionIntegration() );
        }

        if (!StringUtils.isNullOrEmpty( dsc.getDriverClass() )) {
            connectionFactoryConfig.connectionProviderClassName(dsc.getDriverClass());
        } else if (!StringUtils.isNullOrEmpty( dsc.getDataSourceClass( ))) {
            connectionFactoryConfig.connectionProviderClassName(dsc.getDataSourceClass());
        }

        if (!StringUtils.isNullOrEmpty( dsc.getConnectionUrl() )) {
            connectionFactoryConfig.jdbcUrl(dsc.getConnectionUrl());
        }

        setDatabaseCredentials(connectionFactoryConfig, dsc.getUsername(), dsc.getPassword());

        setConnectionPoolConfiguration(poolConfig, connectionFactoryConfig, dsc.getPool(), !jtaPresent);

        dsc.getProps().forEach(connectionFactoryConfig::jdbcProperty);

        try {
            return AgroalDataSource.from( dataSourceConfig );
        } catch (SQLException e) {
            throw new KumuluzServerException("Failed to create DataSource", e);
        }
    }

    public static AgroalDataSource createXaDataSource(XaDataSourceConfig xdsc, boolean jtaPresent) {
        AgroalDataSourceConfigurationSupplier xaDataSourceConfig = new AgroalDataSourceConfigurationSupplier();

        AgroalConnectionPoolConfigurationSupplier poolConfig = xaDataSourceConfig.connectionPoolConfiguration();

        AgroalConnectionFactoryConfigurationSupplier connectionFactoryConfig = poolConfig.connectionFactoryConfiguration();

        if (jtaPresent) {
            poolConfig.transactionIntegration( JtaProvider.getInstance().getTransactionIntegration() );
        }

        if (!StringUtils.isNullOrEmpty( xdsc.getXaDatasourceClass() )) {
            connectionFactoryConfig.connectionProviderClassName( xdsc.getXaDatasourceClass() );
        }

        setDatabaseCredentials(connectionFactoryConfig, xdsc.getUsername(), xdsc.getPassword());

        setConnectionPoolConfiguration(poolConfig, connectionFactoryConfig, xdsc.getPool(), false);

        xdsc.getProps().forEach(connectionFactoryConfig::jdbcProperty);

        try {
            return AgroalDataSource.from( xaDataSourceConfig );
        } catch (SQLException e) {
            throw new KumuluzServerException("Failed to create XaDataSource", e);
        }
    }

    private static void setDatabaseCredentials(AgroalConnectionFactoryConfigurationSupplier connectionFactory, String username, String password) {
        if (!StringUtils.isNullOrEmpty( username )) {
            connectionFactory.principal(new NamePrincipal( username ));
        }

        if (!StringUtils.isNullOrEmpty( password )) {
            connectionFactory.credential(new SimplePassword( password ));
        }
    }

    private static void setConnectionPoolConfiguration(AgroalConnectionPoolConfigurationSupplier pool,
                                                       AgroalConnectionFactoryConfigurationSupplier connectionFactoryConfig,
                                                       DataSourcePoolConfig dscp, boolean isHikariSelected) {

        Optional.ofNullable( dscp.getAutoCommit() ).ifPresent(connectionFactoryConfig::autoCommit);

        Optional.ofNullable( dscp.getFlushOnClose() ).ifPresent(v -> {
            warnMessageAgroalFeature("flushOnClose", isHikariSelected);
            pool.flushOnClose(v);
        });

        Optional.ofNullable( dscp.getConnectionTimeout() ).map(Duration::ofMillis).ifPresent(pool::acquisitionTimeout);

        Optional.ofNullable( dscp.getIdleTimeout() ).map(Duration::ofMillis).ifPresent(pool::reapTimeout);

        Optional.ofNullable( dscp.getMaxLifetime() ).map(Duration::ofMillis).ifPresent(pool::maxLifetime);

        Optional.ofNullable( dscp.getInitialSize() ).ifPresent(v -> {
            warnMessageAgroalFeature("initialSize", isHikariSelected);
            pool.initialSize(v);
        });

        Optional.ofNullable( dscp.getMinSize() ).ifPresent(v -> {
            warnMessageAgroalFeature("minSize", isHikariSelected);
            pool.minSize(v);
        });

        Optional.ofNullable( dscp.getMaxSize() ).ifPresent(pool::maxSize);

        if (!StringUtils.isNullOrEmpty( dscp.getName() )) {
            // Unknown or non-existent configuration in Agroal
            warnMessageDepreciatedProperty("name");
        }

        // Unknown or non-existent configuration in Agroal
        Optional.ofNullable( dscp.getInitializationFailTimeout() ).ifPresent(v -> warnMessageDepreciatedProperty("initializationFailTimeout") );

        // Unknown or non-existent configuration in Agroal
        Optional.ofNullable( dscp.getIsolateInternalQueries() ).ifPresent(v -> warnMessageDepreciatedProperty("isolateInternalQueries") );

        // Unknown or non-existent configuration in Agroal
        Optional.ofNullable( dscp.getAllowPoolSuspension() ).ifPresent(v -> warnMessageDepreciatedProperty("allowPoolSuspension") );

        // Unknown or non-existent configuration in Agroal
        Optional.ofNullable( dscp.getReadOnly() ).ifPresent(v -> warnMessageDepreciatedProperty("readOnly") );

        // Unknown or non-existent configuration in Agroal
        Optional.ofNullable( dscp.getRegisterMbeans() ).ifPresent(v -> warnMessageDepreciatedProperty("registerMbeans") );

        Optional.ofNullable( dscp.getValidationTimeout() ).map(Duration::ofMillis).ifPresent(pool::validationTimeout);

        Optional.ofNullable( dscp.getLeakDetectionThreshold() ).map(Duration::ofMillis).ifPresent(pool::leakTimeout);

        Optional.ofNullable( dscp.getIdleValidationTimeout() ).map(Duration::ofMillis).ifPresent(v -> {
            warnMessageAgroalFeature("idleValidationTimeout", isHikariSelected);
            pool.idleValidationTimeout(v);
        });

        // Unknown or non-existent configuration in Agroal
        Optional.ofNullable( dscp.getMinIdle() ).ifPresent(v -> warnMessageDepreciatedProperty("minIdle") );

        if (dscp.getConnectionValidSql() != null) {
            String validationQuery = dscp.getConnectionValidSql();
            pool.connectionValidator(new AgroalConnectionPoolConfiguration.ConnectionValidator() {

                @Override
                public boolean isValid(Connection connection) {
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute(validationQuery);
                        return true;
                    } catch (Exception e) {
                        log.log(Level.WARNING, "Connection validation failed", e);
                    }
                    return false;
                }
            });
        } else {
            pool.connectionValidator(AgroalConnectionPoolConfiguration.ConnectionValidator.defaultValidator());
        }

        if (!StringUtils.isNullOrEmpty( dscp.getConnectionInitSql() )) {
            connectionFactoryConfig.initialSql(dscp.getConnectionInitSql());
        }

        setJdbcTransactionIsolation(connectionFactoryConfig, dscp.getTransactionIsolation());
    }

    private static void setJdbcTransactionIsolation(AgroalConnectionFactoryConfigurationSupplier connectionFactory, String transactionIsolation) {
        if (!StringUtils.isNullOrEmpty( transactionIsolation )) {

            for (TransactionIsolation isolation : TransactionIsolation.values()) {

                final String isolationName = "TRANSACTION_" + isolation.name();

                if (isolationName.equals(isolation)) {
                    connectionFactory.jdbcTransactionIsolation(isolation);
                    break;
                }
            }
        }
    }

    private static void warnMessageAgroalFeature(String propertyName, boolean isHikariSelected) {
        if (isHikariSelected) {
            log.log(Level.WARNING, "The property \"" + propertyName + "\" is exclusive to the Agroal Connection Pool, therefore it will not be recognized by HikariCP!");
        }
    }

    private static void warnMessageDepreciatedProperty(String propertyName) {
        log.log(Level.WARNING, "Property \"" + propertyName + "\" is not supported by Agroal Connection Pool and will be removed soon!");
    }

}
