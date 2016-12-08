package com.kumuluz.ee.common.config;

import com.kumuluz.ee.common.utils.EnvUtils;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class ServerConfig {

    public static final String PORT_ENV = "PORT";

    public static final String MIN_THREADS_ENV = "MIN_THREADS";

    public static final String MAX_THREADS_ENV = "MAX_THREADS";

    public static final String REQUEST_HEADER_SIZE = "REQUEST_HEADER_SIZE";

    public static final String RESPONSE_HEADER_SIZE = "RESPONSE_HEADER_SIZE";

    public static final String CONTEXT_PATH_ENV = "CONTEXT_PATH";

    private Integer port = 8080;

    private String contextPath = "/";

    private Integer idleTimeout = 60 * 60 * 1000;

    private Integer soLingerTime = -1;

    private Integer minThreads = 5;

    private Integer maxThreads = 100;

    private Integer requestHeaderSize = 8 * 1024;

    private Integer responseHeaderSize = 8 * 1024;

    public ServerConfig() {

        EnvUtils.getEnvAsInteger(PORT_ENV, this::setPort);
        EnvUtils.getEnvAsInteger(MIN_THREADS_ENV, this::setMinThreads);
        EnvUtils.getEnvAsInteger(MAX_THREADS_ENV, this::setMaxThreads);
        EnvUtils.getEnvAsInteger(REQUEST_HEADER_SIZE, this::setRequestHeaderSize);
        EnvUtils.getEnvAsInteger(RESPONSE_HEADER_SIZE, this::setResponseHeaderSize);
        EnvUtils.getEnv(CONTEXT_PATH_ENV, this::setContextPath);
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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
}
