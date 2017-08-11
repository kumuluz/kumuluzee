package com.kumuluz.ee.common.jca.drivers;

import com.kumuluz.ee.common.jca.DriverInfo;

/**
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class H2DriverInfo extends DriverInfo {

    public H2DriverInfo() {
        super("org.h2.Driver", "org.h2.jdbcx.JdbcDataSource", "SELECT 1");
    }

}
