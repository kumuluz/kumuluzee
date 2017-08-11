package com.kumuluz.ee.common.jca.drivers;

import com.kumuluz.ee.common.jca.DriverInfo;

/**
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class DB2DriverInfo extends DriverInfo {

    public DB2DriverInfo() {
        super("com.ibm.db2.jcc.DB2Driver", "com.ibm.db2.jdbc.DB2XADataSource", "SELECT 1 FROM SYSIBM.SYSDUMMY1");
    }

}
