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

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class ServerConfig {

    public static class Builder {

        private String baseUrl;
        private String contextPath = "/";
        private Boolean dirBrowsing = false;
        private Integer minThreads = 5;
        private Integer maxThreads = 100;
        private Boolean forceHttps = false;

        private ServerConnectorConfig.Builder http = new ServerConnectorConfig.Builder();
        private ServerConnectorConfig.Builder https = new ServerConnectorConfig.Builder();

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder contextPath(String contextPath) {
            this.contextPath = contextPath;
            return this;
        }

        public Builder dirBrowsing(Boolean dirBrowsing) {
            this.dirBrowsing = dirBrowsing;
            return this;
        }

        public Builder minThreads(Integer minThreads) {
            this.minThreads = minThreads;
            return this;
        }

        public Builder maxThreads(Integer maxThreads) {
            this.maxThreads = maxThreads;
            return this;
        }

        public Builder forceHttps(Boolean forceHttps) {
            this.forceHttps = forceHttps;
            return this;
        }

        public Builder http(ServerConnectorConfig.Builder http) {
            this.http = http;
            return this;
        }

        public Builder https(ServerConnectorConfig.Builder https) {
            this.https = https;
            return this;
        }

        public ServerConfig build() {

            ServerConfig serverConfig = new ServerConfig();
            serverConfig.baseUrl = baseUrl;
            serverConfig.contextPath = contextPath;
            serverConfig.dirBrowsing = dirBrowsing;
            serverConfig.minThreads = minThreads;
            serverConfig.maxThreads = maxThreads;
            serverConfig.forceHttps = forceHttps;

            serverConfig.http = http.build();
            serverConfig.https = https.build();

            return serverConfig;
        }
    }

    private String baseUrl;
    private String contextPath;
    private Boolean dirBrowsing;
    private Integer minThreads;
    private Integer maxThreads;
    private Boolean forceHttps;

    private ServerConnectorConfig http;
    private ServerConnectorConfig https;

    private ServerConfig() {
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getContextPath() {
        return contextPath;
    }

    public Boolean getDirBrowsing() {
        return dirBrowsing;
    }

    public Integer getMinThreads() {
        return minThreads;
    }

    public Integer getMaxThreads() {
        return maxThreads;
    }

    public Boolean getForceHttps() {
        return forceHttps;
    }

    public ServerConnectorConfig getHttp() {
        return http;
    }

    public ServerConnectorConfig getHttps() {
        return https;
    }
}
