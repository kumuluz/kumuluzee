package com.kumuluz.ee.common.jca.drivers;

import com.kumuluz.ee.common.jca.DriverInfo;

/**
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class PostgreSQLDriverInfo extends DriverInfo {

    public PostgreSQLDriverInfo() {
        super("org.postgresql.Driver", "org.postgresql.xa.PGXADataSource", "SELECT 1");
    }

}
