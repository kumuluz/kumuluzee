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

import com.kumuluz.ee.common.utils.EnvUtils;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
@Deprecated
public class PersistenceConfig {

    public static class Builder {

        private String unitName;
        private String url;
        private String username;
        private String password;

        public Builder unitName(String unitName) {
            this.unitName = unitName;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
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

        public PersistenceConfig build() {

            PersistenceConfig persistenceConfig = new PersistenceConfig();
            persistenceConfig.unitName = unitName;
            persistenceConfig.url = url;
            persistenceConfig.username = username;
            persistenceConfig.password = password;

            return persistenceConfig;
        }
    }

    private String unitName;
    private String url;
    private String username;
    private String password;

    public String getUnitName() {
        return unitName;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
