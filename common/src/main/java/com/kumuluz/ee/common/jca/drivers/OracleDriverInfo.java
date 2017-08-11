package com.kumuluz.ee.common.jca.drivers;

import com.kumuluz.ee.common.jca.DriverInfo;

/**
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class OracleDriverInfo extends DriverInfo {

    public OracleDriverInfo() {
        super("oracle.jdbc.OracleDriver", "oracle.jdbc.xa.client.OracleXADataSource", "SELECT 'Hello' from DUAL");
    }
    
}
