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

    private Integer port = 8080;

    private Integer sslPort = 8443;

    private String contextPath = "/";

    private Integer idleTimeout = 60 * 60 * 1000;

    private Integer soLingerTime = -1;

    private Integer minThreads = 5;

    private Integer maxThreads = 100;

    private Integer requestHeaderSize = 8 * 1024;

    private Integer responseHeaderSize = 8 * 1024;

    private String keystorePath = System.getProperty("javax.net.ssl.keyStore");

    private String keystorePassword = System.getProperty("javax.net.ssl.keyStorePassword");

    private String keyManagerPassword;

    private Boolean enableSSL = false;

    private Boolean forceSSL = false;

    public ServerConfig() {
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getSSLPort() {
        return sslPort;
    }

    public void setSSLPort(Integer sslPort) {
        this.sslPort = sslPort;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public Integer getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Integer idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public Integer getSoLingerTime() {
        return soLingerTime;
    }

    public void setSoLingerTime(Integer soLingerTime) {
        this.soLingerTime = soLingerTime;
    }

    public Integer getMinThreads() {
        return minThreads;
    }

    public void setMinThreads(Integer minThreads) {
        this.minThreads = minThreads;
    }

    public Integer getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(Integer maxThreads) {
        this.maxThreads = maxThreads;
    }

    public Integer getRequestHeaderSize() {
        return requestHeaderSize;
    }

    public void setRequestHeaderSize(Integer requestHeaderSize) {
        this.requestHeaderSize = requestHeaderSize;
    }

    public Integer getResponseHeaderSize() {
        return responseHeaderSize;
    }

    public void setResponseHeaderSize(Integer responseHeaderSize) {
        this.responseHeaderSize = responseHeaderSize;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public String getKeyManagerPassword() {
        return keyManagerPassword;
    }

    public void setKeyManagerPassword(String keyManagerPassword) {
        this.keyManagerPassword = keyManagerPassword;
    }

    public Boolean getEnableSSL() {
        return enableSSL;
    }

    public void setEnableSSL(Boolean enableSSL) {
        this.enableSSL = enableSSL;
    }

    public Boolean getForceSSL() {
        return forceSSL;
    }

    public void setForceSSL(Boolean forceSSL) {
        this.forceSSL = forceSSL;
    }
}
