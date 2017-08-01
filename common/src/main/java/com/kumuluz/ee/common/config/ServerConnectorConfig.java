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
import java.util.List;

/**
 * @author Tilen Faganel
 * @since 2.4.0
 */
public class ServerConnectorConfig {

    public static class Builder {

        public final static Integer DEFAULT_HTTP_PORT = 8080;
        public final static Integer DEFAULT_HTTPS_PORT = 8443;

        private Integer port = 8080;
        private String address;
        private Boolean enabled;
        private Boolean http2 = false;
        private Boolean proxyForwarding = false;
        private Integer requestHeaderSize = 8 * 1024;
        private Integer responseHeaderSize = 8 * 1024;
        private Integer idleTimeout = 60 * 60 * 1000;
        private Integer soLingerTime = -1;

        private String keystorePath = System.getProperty("javax.net.ssl.keyStore");
        private String keystorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        private String keyAlias;
        private String keyPassword;
        private List<String> sslProtocols;
        private List<String> sslCiphers;

        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder http2(Boolean http2) {
            this.http2 = http2;
            return this;
        }

        public Builder proxyForwarding(Boolean proxyForwarding) {
            this.proxyForwarding = proxyForwarding;
            return this;
        }

        public Builder requestHeaderSize(Integer requestHeaderSize) {
            this.requestHeaderSize = requestHeaderSize;
            return this;
        }

        public Builder responseHeaderSize(Integer responseHeaderSize) {
            this.responseHeaderSize = responseHeaderSize;
            return this;
        }

        public Builder idleTimeout(Integer idleTimeout) {
            this.idleTimeout = idleTimeout;
            return this;
        }

        public Builder soLingerTime(Integer soLingerTime) {
            this.soLingerTime = soLingerTime;
            return this;
        }

        public Builder keystorePath(String keystorePath) {
            this.keystorePath = keystorePath;
            return this;
        }

        public Builder keystorePassword(String keystorePassword) {
            this.keystorePassword = keystorePassword;
            return this;
        }

        public Builder keyAlias(String keyAlias) {
            this.keyAlias = keyAlias;
            return this;
        }

        public Builder keyPassword(String keyPassword) {
            this.keyPassword = keyPassword;
            return this;
        }

        public Builder sslProtocols(List<String> sslProtocols) {
            this.sslProtocols = Collections.unmodifiableList(sslProtocols);
            return this;
        }

        public Builder sslCiphers(List<String> sslCiphers) {
            this.sslCiphers = Collections.unmodifiableList(sslCiphers);
            return this;
        }

        public ServerConnectorConfig build() {

            ServerConnectorConfig serverConnectorConfig = new ServerConnectorConfig();
            serverConnectorConfig.port = port;
            serverConnectorConfig.address = address;
            serverConnectorConfig.enabled = enabled;
            serverConnectorConfig.http2 = http2;
            serverConnectorConfig.proxyForwarding = proxyForwarding;
            serverConnectorConfig.requestHeaderSize = requestHeaderSize;
            serverConnectorConfig.responseHeaderSize = responseHeaderSize;
            serverConnectorConfig.idleTimeout = idleTimeout;
            serverConnectorConfig.soLingerTime = soLingerTime;
            serverConnectorConfig.keystorePath = keystorePath;
            serverConnectorConfig.keystorePassword = keystorePassword;
            serverConnectorConfig.keyAlias = keyAlias;
            serverConnectorConfig.keyPassword = keyPassword;
            serverConnectorConfig.sslProtocols = sslProtocols;
            serverConnectorConfig.sslCiphers = sslCiphers;

            return serverConnectorConfig;
        }
    }

    private Integer port;
    private String address;
    private Boolean enabled;
    private Boolean http2;
    private Boolean proxyForwarding;
    private Integer requestHeaderSize;
    private Integer responseHeaderSize;
    private Integer idleTimeout;
    private Integer soLingerTime;

    private String keystorePath;
    private String keystorePassword;
    private String keyAlias;
    private String keyPassword;
    private List<String> sslProtocols;
    private List<String> sslCiphers;

    public Integer getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean getHttp2() {
        return http2;
    }

    public Boolean getProxyForwarding() {
        return proxyForwarding;
    }

    public Integer getRequestHeaderSize() {
        return requestHeaderSize;
    }

    public Integer getResponseHeaderSize() {
        return responseHeaderSize;
    }

    public Integer getIdleTimeout() {
        return idleTimeout;
    }

    public Integer getSoLingerTime() {
        return soLingerTime;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public List<String> getSslProtocols() {
        return sslProtocols;
    }

    public List<String> getSslCiphers() {
        return sslCiphers;
    }
}
