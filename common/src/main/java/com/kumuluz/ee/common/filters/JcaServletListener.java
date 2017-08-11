package com.kumuluz.ee.common.filters;

import com.kumuluz.ee.common.config.DataSourceConfig;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.config.XaDataSourceConfig;
import com.kumuluz.ee.common.jca.JcaEmbeddedDeployer;
import com.kumuluz.ee.common.jca.DriverInfo;
import com.kumuluz.ee.common.jca.ResourceAdapterHelper;
import com.kumuluz.ee.common.runtime.EeRuntimeInternal;
import com.kumuluz.ee.common.utils.StringUtils;
import org.jboss.jca.embedded.Embedded;
import org.jboss.jca.embedded.dsl.datasources13.api.DatasourceType;
import org.jboss.jca.embedded.dsl.datasources13.api.DatasourcesDescriptor;
import org.jboss.jca.embedded.dsl.datasources13.api.XaDatasourceType;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

import javax.servlet.*;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class JcaServletListener implements ServletContextListener {

    private EeRuntimeInternal eeRuntimeInternal;
    private EeConfig eeConfig;
    private boolean jtaPresent;

    public JcaServletListener(EeRuntimeInternal eeRuntimeInternal, EeConfig eeConfig, boolean jtaPresent) {
        this.eeRuntimeInternal = eeRuntimeInternal;
        this.eeConfig = eeConfig;
        this.jtaPresent = jtaPresent;

        // Initiate the IronJacamar
        JcaEmbeddedDeployer jca = JcaEmbeddedDeployer.getInstance();
        jca.start();

        // Deploy IronJacamar's profiles
        jca.deploy(Embedded.class, "naming.xml");
        jca.deploy(Embedded.class, (jtaPresent ? "transaction.xml" : "noop-transaction.xml"));
        jca.deploy(JcaServletListener.class, "jca.xml");
        jca.deploy(Embedded.class, "ds.xml");

        // Deploy JDBC Local/XA Resource Adapters
        if (!eeConfig.getDatasources().isEmpty()) {
            jca.deploy(ResourceAdapterHelper.getJdbcLocalRAR());
        }

        if (!eeConfig.getXaDatasources().isEmpty()) {
            jca.deploy(ResourceAdapterHelper.getJdbcXaRAR());
        }

        // Deploy Local/XA Datasources
        jca.deploy(createDatasourcesDescriptor());
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        JcaEmbeddedDeployer.getInstance().stop();
    }

    private DatasourcesDescriptor createDatasourcesDescriptor() {
        String descriptorName = String.format("kumuluz-%s-ds.xml", eeRuntimeInternal.getInstanceId());

        DatasourcesDescriptor descriptor = Descriptors.create(DatasourcesDescriptor.class, descriptorName);

        eeConfig.getDatasources().forEach(dsc -> addLocalDataSource(descriptor, dsc));

        eeConfig.getXaDatasources().forEach(xdsc -> addXaDataSource(descriptor, xdsc));

        return descriptor;
    }

    private void addLocalDataSource(DatasourcesDescriptor descriptor, DataSourceConfig dsc) {
        DatasourceType<DatasourcesDescriptor> ds = descriptor.createDatasource()
                .jndiName(dsc.getJndiName())
                .poolName(UUID.randomUUID().toString())
                .jta(jtaPresent);

        DriverInfo.fromJdbcUrl(dsc.getConnectionUrl()).ifPresent(driver -> {
            ds.driverClass(driver.getDriverClassName());

            ds.newConnectionSql(driver.getCheckConnectionSql());
            ds.getOrCreateValidation().checkValidConnectionSql(driver.getCheckConnectionSql());
        });

        ds.connectionUrl(dsc.getConnectionUrl());

        if (!StringUtils.isNullOrEmpty(dsc.getDriverClass())) {
            ds.driverClass(dsc.getDriverClass());
        }

        ds.getOrCreateSecurity()
                .userName(dsc.getUsername())
                .password(dsc.getPassword());

        // Conection pool properties
        Optional.ofNullable(dsc.getInitialPoolSize()).ifPresent(v -> ds.getOrCreatePool().initialPoolSize(v));
        Optional.ofNullable(dsc.getMinPoolSize()).ifPresent(v -> ds.getOrCreatePool().minPoolSize(v));
        Optional.ofNullable(dsc.getMaxPoolSize()).ifPresent(v -> ds.getOrCreatePool().maxPoolSize(v));

        // Connection validation properties
        if (!StringUtils.isNullOrEmpty(dsc.getCheckConnectionSql())) {
            ds.newConnectionSql(dsc.getCheckConnectionSql());
            ds.getOrCreateValidation().checkValidConnectionSql(dsc.getCheckConnectionSql());
        }

        // Timeout properties
        Optional.ofNullable(dsc.getBlockingTimeoutMillis()).ifPresent(v -> ds.getOrCreateTimeout().blockingTimeoutMillis(v));
        Optional.ofNullable(dsc.getIdleTimeoutMinutes()).ifPresent(v -> ds.getOrCreateTimeout().idleTimeoutMinutes(v));
    }

    private void addXaDataSource(DatasourcesDescriptor descriptor, XaDataSourceConfig xdsc) {
        XaDatasourceType<DatasourcesDescriptor> ds = descriptor.createXaDatasource()
                .jndiName(xdsc.getJndiName())
                .poolName(UUID.randomUUID().toString());

        DriverInfo.fromXaDataSourceClass(xdsc.getXaDatasourceClass()).ifPresent(driver -> {
            ds.newConnectionSql(driver.getCheckConnectionSql());
            ds.getOrCreateValidation().checkValidConnectionSql(driver.getCheckConnectionSql());
        });

        ds.xaDatasourceClass(xdsc.getXaDatasourceClass());

		/*
		 * Uses "xa-datasource-property" because IronJacamar doesn't send security info correctly
		 */
        ds.createXaDatasourceProperty().name("User").text(xdsc.getUsername());
        ds.createXaDatasourceProperty().name("Password").text(xdsc.getPassword());

        for (Map.Entry<String, String> property : xdsc.getProps().entrySet()) {
            ds.createXaDatasourceProperty().name(property.getKey()).text(property.getValue());
        }

        // Conection pool properties
        Optional.ofNullable(xdsc.getInitialPoolSize()).ifPresent(v -> ds.getOrCreateXaPool().initialPoolSize(v));
        Optional.ofNullable(xdsc.getMinPoolSize()).ifPresent(v -> ds.getOrCreateXaPool().minPoolSize(v));
        Optional.ofNullable(xdsc.getMaxPoolSize()).ifPresent(v -> ds.getOrCreateXaPool().maxPoolSize(v));

        // Connection validation properties
        if (!StringUtils.isNullOrEmpty(xdsc.getCheckConnectionSql())) {
            ds.newConnectionSql(xdsc.getCheckConnectionSql());
            ds.getOrCreateValidation().checkValidConnectionSql(xdsc.getCheckConnectionSql());
        }

        // Timeout properties
        Optional.ofNullable(xdsc.getBlockingTimeoutMillis()).ifPresent(v -> ds.getOrCreateTimeout().blockingTimeoutMillis(v));
        Optional.ofNullable(xdsc.getIdleTimeoutMinutes()).ifPresent(v -> ds.getOrCreateTimeout().idleTimeoutMinutes(v));
    }

}
