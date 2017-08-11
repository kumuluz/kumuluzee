package com.kumuluz.ee.common.jca;

import com.kumuluz.ee.common.utils.StringUtils;

import java.util.*;

/**
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class DriverInfo {

    private static Set<DriverInfo> drivers;

    private String driverClassName;
    private String xaDataSourceClassName;
    private String checkConnectionSql;

    public DriverInfo(String driverClassName, String xaDataSourceClassName, String checkConnectionSql) {
        Objects.requireNonNull(driverClassName, "Driver Class is required!");
        Objects.requireNonNull(checkConnectionSql, "Check Connection SQL is required!");

        this.driverClassName = driverClassName;
        this.xaDataSourceClassName = xaDataSourceClassName;
        this.checkConnectionSql = checkConnectionSql;
    }

    public String getProductName() {
        return getClass().getSimpleName().replace(DriverInfo.class.getSimpleName(), "").toLowerCase();
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getXaDataSourceClassName() {
        return xaDataSourceClassName;
    }

    public String getCheckConnectionSql() {
        return checkConnectionSql;
    }

    public boolean matcheJdbcUrl(String jdbcUrl) {
        if (StringUtils.isNullOrEmpty(jdbcUrl)) {
            return false;
        }
        return jdbcUrl.matches(String.format("^(jdbc:%s:).*", getProductName()));
    }

    private static Set<DriverInfo> getJdbcDrivers() {
        if (Objects.isNull(drivers)) {
            drivers = new HashSet<>();
            ServiceLoader.load(DriverInfo.class).forEach(drivers::add);
        }
        return drivers;
    }

    public static Optional<DriverInfo> fromJdbcUrl(String jdbcUrl) {
        if (StringUtils.isNullOrEmpty(jdbcUrl)) {
            return Optional.empty();
        }
        return getJdbcDrivers().stream()
                .filter(driver -> driver.matcheJdbcUrl(jdbcUrl))
                .findFirst();
    }

    public static Optional<DriverInfo> fromXaDataSourceClass(String xaDsClassName) {
        if (StringUtils.isNullOrEmpty(xaDsClassName)) {
            return Optional.empty();
        }
        return getJdbcDrivers().stream()
                .filter(driver -> Objects.equals(driver.getXaDataSourceClassName(), xaDsClassName))
                .findFirst();
    }
}
