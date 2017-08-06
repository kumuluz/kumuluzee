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
package com.kumuluz.ee.common.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public class XaDataSourceConfig {

    public static class Builder {

        private String jndiName;
        private String xaDatasourceClass;
        private String username;
        private String password;

        private Map<String, String> props = new HashMap<>();

        public Builder jndiName(String jndiName) {
            this.jndiName = jndiName;
            return this;
        }

        public Builder xaDatasourceClass(String xaDatasourceClass) {
            this.xaDatasourceClass = xaDatasourceClass;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder prop(String key, String value) {
            this.props.put(key, value);
            return this;
        }

        public XaDataSourceConfig build() {

            XaDataSourceConfig xaDataSourceConfig = new XaDataSourceConfig();
            xaDataSourceConfig.jndiName = jndiName;
            xaDataSourceConfig.xaDatasourceClass = xaDatasourceClass;
            xaDataSourceConfig.username = username;
            xaDataSourceConfig.password = password;

            xaDataSourceConfig.props = Collections.unmodifiableMap(props);

            return xaDataSourceConfig;
        }
    }

    private String jndiName;
    private String xaDatasourceClass;
    private String username;
    private String password;

    private Map<String, String> props;

    private XaDataSourceConfig() {
    }

    public String getJndiName() {
        return jndiName;
    }

    public String getXaDatasourceClass() {
        return xaDatasourceClass;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, String> getProps() {
        return props;
    }
}
