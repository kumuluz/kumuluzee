/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
*/
package com.kumuluz.ee.jta.common.datasources;

import com.kumuluz.ee.common.datasources.NonJtaXADataSourceWrapper;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
@Deprecated
public class JtaXADataSourceWrapper extends NonJtaXADataSourceWrapper {

    public JtaXADataSourceWrapper(XADataSource xaDataSource) {
        super(xaDataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {

        checkIfValid();

        XAConnection xaConnection = xaDataSource.getXAConnection();

        return new JtaXAConnectionWrapper(xaConnection);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {

        checkIfValid();

        XAConnection xaConnection = xaDataSource.getXAConnection(username, password);

        return new JtaXAConnectionWrapper(xaConnection);
    }
}
