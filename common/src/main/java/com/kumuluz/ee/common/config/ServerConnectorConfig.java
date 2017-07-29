package com.kumuluz.ee.common.config;

/**
 * @author Tilen Faganel
 * @since 2.4.0
 */
public class ServerConnectorConfig {

    public static class Builder {

        public final static Integer DEFAULT_HTTP_PORT = 8080;
        public final static Integer DEFAULT_HTTPS_PORT = 8443;

        private Integer port;
        private String address;
        private Boolean enabled;
        private Integer requestHeaderSize = 8 * 1024;
        private Integer responseHeaderSize = 8 * 1024;
        private Integer idleTimeout = 60 * 60 * 1000;
        private Integer soLingerTime = -1;

        private String keystorePath = System.getProperty("javax.net.ssl.keyStore");
        private String keystorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        private String keyAlias;
        private String keyPassword;

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

        public ServerConnectorConfig build() {

            ServerConnectorConfig serverConnectorConfig = new ServerConnectorConfig();
            serverConnectorConfig.port = port;
            serverConnectorConfig.address = address;
            serverConnectorConfig.enabled = enabled;
            serverConnectorConfig.requestHeaderSize = requestHeaderSize;
            serverConnectorConfig.responseHeaderSize = responseHeaderSize;
            serverConnectorConfig.idleTimeout = idleTimeout;
            serverConnectorConfig.soLingerTime = soLingerTime;
            serverConnectorConfig.keystorePath = keystorePath;
            serverConnectorConfig.keystorePassword = keystorePassword;
            serverConnectorConfig.keyAlias = keyAlias;
            serverConnectorConfig.keyPassword = keyPassword;

            return serverConnectorConfig;
        }
    }

    private Integer port;
    private String address;
    private Boolean enabled;
    private Integer requestHeaderSize;
    private Integer responseHeaderSize;
    private Integer idleTimeout;
    private Integer soLingerTime;

    private String keystorePath;
    private String keystorePassword;
    private String keyAlias;
    private String keyPassword;

    public Integer getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public Boolean getEnabled() {
        return enabled;
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
}
