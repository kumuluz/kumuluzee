package com.kumuluz.ee.common.config;

public class MailServiceConfig {

    public static class Builder {

        private String protocol;
        private String host;
        private Integer port;
        private Boolean starttls;
        private String username;
        private String password;
        private Long connectionTimeout;
        private Long timeout;

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        public Builder starttls(Boolean starttls) {
            this.starttls = starttls;
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

        public Builder connectionTimeout(Long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder timeout(Long timeout) {
            this.timeout = timeout;
            return this;
        }

        public MailServiceConfig build() {

            MailServiceConfig mailServiceConfig = new MailServiceConfig();
            mailServiceConfig.protocol = protocol;
            mailServiceConfig.host = host;
            mailServiceConfig.port = port;
            mailServiceConfig.starttls = starttls;
            mailServiceConfig.username = username;
            mailServiceConfig.password = password;
            mailServiceConfig.connectionTimeout = connectionTimeout;
            mailServiceConfig.timeout = timeout;

            return mailServiceConfig;
        }
    }

    private String protocol;
    private String host;
    private Integer port;
    private Boolean starttls;
    private String username;
    private String password;
    private Long connectionTimeout;
    private Long timeout;

    private MailServiceConfig(){
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public Boolean getStarttls() {
        return starttls;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Long getConnectionTimeout() {
        return connectionTimeout;
    }

    public Long getTimeout() {
        return timeout;
    }
}
