package com.kumuluz.ee.common.jca.drivers;

import com.kumuluz.ee.common.jca.DriverInfo;

/**
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class SybaseDriverInfo extends DriverInfo {

    public SybaseDriverInfo() {
        super("com.sybase.jdbc4.jdbc.SybDriver", "com.sybase.jdbc4.jdbc.SybXADataSource", "SELECT 1");
    }

}
