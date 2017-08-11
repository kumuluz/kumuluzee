package com.kumuluz.ee.common.jca.drivers;

import com.kumuluz.ee.common.jca.DriverInfo;

/**
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class DerbyDriverInfo extends DriverInfo {

    public DerbyDriverInfo() {
        super("org.apache.derby.jdbc.ClientDriver", "org.apache.derby.jdbc.ClientXADataSource", "SELECT 1 FROM SYSIBM.SYSDUMMY1");
    }

}
