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
public class ServerConfig {

    public static final String PORT_ENV = "PORT";

    public static final String SSL_PORT_ENV = "SSL_PORT";

    public static final String MIN_THREADS_ENV = "MIN_THREADS";

    public static final String MAX_THREADS_ENV = "MAX_THREADS";

    public static final String REQUEST_HEADER_SIZE = "REQUEST_HEADER_SIZE";

    public static final String RESPONSE_HEADER_SIZE = "RESPONSE_HEADER_SIZE";

    public static final String CONTEXT_PATH_ENV = "CONTEXT_PATH";

    public static final String KEYSTORE_PATH_ENV = "KEYSTORE_PATH";

    public static final String KEYSTORE_PASSWORD_ENV = "KEYSTORE_PASSWORD";

    public static final String KEY_MANAGER_PASS_ENV = "KEY_MANAGER_PASSWORD";

    public static final String ENABLE_SSL_ENV = "ENABLE_SSL";

    public static final String FORCE_SSL_ENV = "FORCE_SSL";

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

        EnvUtils.getEnvAsInteger(PORT_ENV, this::setPort);
        EnvUtils.getEnvAsInteger(SSL_PORT_ENV, this::setSSLPort);
        EnvUtils.getEnvAsInteger(MIN_THREADS_ENV, this::setMinThreads);
        EnvUtils.getEnvAsInteger(MAX_THREADS_ENV, this::setMaxThreads);
        EnvUtils.getEnvAsInteger(REQUEST_HEADER_SIZE, this::setRequestHeaderSize);
        EnvUtils.getEnvAsInteger(RESPONSE_HEADER_SIZE, this::setResponseHeaderSize);
        EnvUtils.getEnv(CONTEXT_PATH_ENV, this::setContextPath);
        EnvUtils.getEnv(KEYSTORE_PATH_ENV, this::setKeystorePath);
        EnvUtils.getEnv(KEYSTORE_PASSWORD_ENV, this::setKeystorePassword);
        EnvUtils.getEnv(KEY_MANAGER_PASS_ENV, this::setKeyManagerPassword);
        EnvUtils.getEnvAsBoolean(ENABLE_SSL_ENV, this::setEnableSSL);
        EnvUtils.getEnvAsBoolean(FORCE_SSL_ENV, this::setForceSSL);
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
