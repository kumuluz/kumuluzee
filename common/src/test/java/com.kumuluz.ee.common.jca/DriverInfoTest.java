package com.kumuluz.ee.common.jca;

import com.kumuluz.ee.common.jca.drivers.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

/**
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class DriverInfoTest {

    private DriverInfo findByUrl(String url) {
        Optional<DriverInfo> driver = DriverInfo.fromJdbcUrl(url);
        return (driver.isPresent() ? driver.get() : null);
    }

    private DriverInfo findByXaDsClass(String xaDsClass) {
        Optional<DriverInfo> driver = DriverInfo.fromXaDataSourceClass(xaDsClass);
        return (driver.isPresent() ? driver.get() : null);
    }

    private void assertDriverInfo(Class<?> expected, DriverInfo actual) {
        Assert.assertNotNull("DriverInfo not found", actual);

        Assert.assertEquals(expected.getName(), actual.getClass().getName());
    }

    @Test
    public void db2DriverByUrl() {
        DriverInfo driver = findByUrl("jdbc:db2:ibmdb2db");
        assertDriverInfo(DB2DriverInfo.class, driver);
    }

    @Test
    public void db2DriverByXaClass() {
        DriverInfo driver = findByXaDsClass("com.ibm.db2.jdbc.DB2XADataSource");
        assertDriverInfo(DB2DriverInfo.class, driver);
    }

    @Test
    public void derbyDriverByUrl() {
        DriverInfo driver = findByUrl("jdbc:derby://localhost:1527/db");
        assertDriverInfo(DerbyDriverInfo.class, driver);
    }

    @Test
    public void derbyDriverByXaClass() {
        DriverInfo driver = findByXaDsClass("org.apache.derby.jdbc.ClientXADataSource");
        assertDriverInfo(DerbyDriverInfo.class, driver);
    }

    @Test
    public void h2DriverByUrl() {
        DriverInfo driver = findByUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        assertDriverInfo(H2DriverInfo.class, driver);
    }

    @Test
    public void h2DriverByXaClass() {
        DriverInfo driver = findByXaDsClass("org.h2.jdbcx.JdbcDataSource");
        assertDriverInfo(H2DriverInfo.class, driver);
    }

    @Test
    public void mariaDbDriverByUrl() {
        DriverInfo driver = findByUrl("jdbc:mariadb://localhost:3306/test");
        assertDriverInfo(MariaDBDriverInfo.class, driver);
    }

    @Test
    public void mariaDbDriverByXaClass() {
        DriverInfo driver = findByXaDsClass("org.mariadb.jdbc.MariaDbDataSource");
        assertDriverInfo(MariaDBDriverInfo.class, driver);
    }

    @Test
    public void mySqlDriverByUrl() {
        DriverInfo driver = findByUrl("jdbc:mysql://localhost:3306/test");
        assertDriverInfo(MySQLDriverInfo.class, driver);
    }

    @Test
    public void mySqlDriverByXaClass() {
        DriverInfo driver = findByXaDsClass("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
        assertDriverInfo(MySQLDriverInfo.class, driver);
    }

    @Test
    public void oracleDriverByUrl() {
        DriverInfo driver = findByUrl("jdbc:oracle:thin:@localhost:1521:test");
        assertDriverInfo(OracleDriverInfo.class, driver);
    }

    @Test
    public void oracleDriverByXaClass() {
        DriverInfo driver = findByXaDsClass("oracle.jdbc.xa.client.OracleXADataSource");
        assertDriverInfo(OracleDriverInfo.class, driver);
    }

    @Test
    public void postgresDriverByUrl() {
        DriverInfo driver = findByUrl("jdbc:postgresql://localhost:5432/test");
        assertDriverInfo(PostgreSQLDriverInfo.class, driver);
    }

    @Test
    public void postgresDriverByXaClass() {
        DriverInfo driver = findByXaDsClass("org.postgresql.xa.PGXADataSource");
        assertDriverInfo(PostgreSQLDriverInfo.class, driver);
    }

    @Test
    public void sqlServerDriverByUrl() {
        DriverInfo driver = findByUrl("jdbc:sqlserver://localhost:1433;DatabaseName=test");
        assertDriverInfo(SQLServerDriverInfo.class, driver);
    }

    @Test
    public void sqlServerDriverByXaClass() {
        DriverInfo driver = findByXaDsClass("com.microsoft.sqlserver.jdbc.SQLServerXADataSource");
        assertDriverInfo(SQLServerDriverInfo.class, driver);
    }

    @Test
    public void sybaseDriverByUrl() {
        DriverInfo driver = findByUrl("jdbc:sybase:Tds:localhost:2638/TEST");
        assertDriverInfo(SybaseDriverInfo.class, driver);
    }

    @Test
    public void sybaseDriverByXaClass() {
        DriverInfo driver = findByXaDsClass("com.sybase.jdbc4.jdbc.SybXADataSource");
        assertDriverInfo(SybaseDriverInfo.class, driver);
    }

}
