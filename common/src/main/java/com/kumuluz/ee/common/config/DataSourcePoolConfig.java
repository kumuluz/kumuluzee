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
 * @since 2.4.0
 */
public class DataSourcePoolConfig {

    public static class Builder {

        private Boolean autoCommit = true;
        private Boolean flushOnClose = false;
        private Long connectionTimeout = 30000L;
        private Long idleTimeout = 600000L;

        private Long maxLifetime = 1800000L;

        @Deprecated
        private Integer minIdle;

        private Integer initialSize = 0;
        private Integer minSize = 0;
        private Integer maxSize = 10;

        @Deprecated
        private String name;

        @Deprecated
        private Long initializationFailTimeout;

        @Deprecated
        private Boolean isolateInternalQueries;

        @Deprecated
        private Boolean allowPoolSuspension;

        @Deprecated
        private Boolean readOnly;

        @Deprecated
        private Boolean registerMbeans;
        private String connectionInitSql;
        private String transactionIsolation;
        private Long validationTimeout = 5000L;
        private Long leakDetectionThreshold = 0L;
        private Long idleValidationTimeout = 0L;

        public Builder autoCommit(Boolean autoCommit) {
            this.autoCommit = autoCommit;
            return this;
        }

        public Builder flushOnClose(Boolean flushOnClose) {
            this.flushOnClose = flushOnClose;
            return this;
        }

        public Builder connectionTimeout(Long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder idleTimeout(Long idleTimeout) {
            this.idleTimeout = idleTimeout;
            return this;
        }

        public Builder maxLifetime(Long maxLifetime) {
            this.maxLifetime = maxLifetime;
            return this;
        }

        @Deprecated
        public Builder minIdle(Integer minIdle) {
            this.minIdle = minIdle;
            return this;
        }

        public Builder initialSize(Integer initialSize) {
            this.initialSize = initialSize;
            return this;
        }

        public Builder minSize(Integer minSize) {
            this.minSize = minSize;
            return this;
        }

        public Builder maxSize(Integer maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        @Deprecated
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @Deprecated
        public Builder initializationFailTimeout(Long initializationFailTimeout) {
            this.initializationFailTimeout = initializationFailTimeout;
            return this;
        }

        @Deprecated
        public Builder isolateInternalQueries(Boolean isolateInternalQueries) {
            this.isolateInternalQueries = isolateInternalQueries;
            return this;
        }

        @Deprecated
        public Builder allowPoolSuspension(Boolean allowPoolSuspension) {
            this.allowPoolSuspension = allowPoolSuspension;
            return this;
        }

        @Deprecated
        public Builder readOnly(Boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        @Deprecated
        public Builder registerMbeans(Boolean registerMbeans) {
            this.registerMbeans = registerMbeans;
            return this;
        }

        public Builder connectionInitSql(String connectionInitSql) {
            this.connectionInitSql = connectionInitSql;
            return this;
        }

        public Builder transactionIsolation(String transactionIsolation) {
            this.transactionIsolation = transactionIsolation;
            return this;
        }

        public Builder validationTimeout(Long validationTimeout) {
            this.validationTimeout = validationTimeout;
            return this;
        }

        public Builder leakDetectionThreshold(Long leakDetectionThreshold) {
            this.leakDetectionThreshold = leakDetectionThreshold;
            return this;
        }

        public Builder idleValidationTimeout (Long idleValidationTimeout ) {
            this.idleValidationTimeout  = idleValidationTimeout ;
            return this;
        }

        public DataSourcePoolConfig build() {

            DataSourcePoolConfig dataSourcePoolConfig = new DataSourcePoolConfig();
            dataSourcePoolConfig.autoCommit = autoCommit;
            dataSourcePoolConfig.flushOnClose = flushOnClose;
            dataSourcePoolConfig.connectionTimeout = connectionTimeout;
            dataSourcePoolConfig.idleTimeout = idleTimeout;
            dataSourcePoolConfig.maxLifetime = maxLifetime;
            dataSourcePoolConfig.minIdle = minIdle;
            dataSourcePoolConfig.initialSize = initialSize;
            dataSourcePoolConfig.minSize = minSize;
            dataSourcePoolConfig.maxSize = maxSize;
            dataSourcePoolConfig.name = name;
            dataSourcePoolConfig.initializationFailTimeout = initializationFailTimeout;
            dataSourcePoolConfig.isolateInternalQueries = isolateInternalQueries;
            dataSourcePoolConfig.allowPoolSuspension = allowPoolSuspension;
            dataSourcePoolConfig.readOnly = readOnly;
            dataSourcePoolConfig.registerMbeans = registerMbeans;
            dataSourcePoolConfig.connectionInitSql = connectionInitSql;
            dataSourcePoolConfig.transactionIsolation = transactionIsolation;
            dataSourcePoolConfig.validationTimeout = validationTimeout;
            dataSourcePoolConfig.leakDetectionThreshold = leakDetectionThreshold;
            dataSourcePoolConfig.idleValidationTimeout = idleValidationTimeout;

            return dataSourcePoolConfig;
        }
    }

    private Boolean autoCommit;
    private Boolean flushOnClose;
    private Long connectionTimeout;
    private Long idleTimeout;

    private Long maxLifetime;

    @Deprecated
    private Integer minIdle;

    private Integer initialSize = 0;
    private Integer minSize = 0;
    private Integer maxSize;

    @Deprecated
    private String name;

    @Deprecated
    private Long initializationFailTimeout;

    @Deprecated
    private Boolean isolateInternalQueries;

    @Deprecated
    private Boolean allowPoolSuspension;

    @Deprecated
    private Boolean readOnly;

    @Deprecated
    private Boolean registerMbeans;
    private String connectionInitSql;
    private String transactionIsolation;
    private Long validationTimeout;
    private Long leakDetectionThreshold;
    private Long idleValidationTimeout;

    private DataSourcePoolConfig() {
    }

    public Boolean getFlushOnClose() {
        return flushOnClose;
    }

    public Boolean getAutoCommit() {
        return autoCommit;
    }

    public Long getConnectionTimeout() {
        return connectionTimeout;
    }

    public Long getIdleTimeout() {
        return idleTimeout;
    }

    public Long getMaxLifetime() {
        return maxLifetime;
    }

    @Deprecated
    public Integer getMinIdle() {
        return minIdle;
    }

    public Integer getInitialSize() {
        return initialSize;
    }

    public Integer getMinSize() {
        return minSize;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    @Deprecated
    public String getName() {
        return name;
    }

    @Deprecated
    public Long getInitializationFailTimeout() {
        return initializationFailTimeout;
    }

    @Deprecated
    public Boolean getIsolateInternalQueries() {
        return isolateInternalQueries;
    }

    @Deprecated
    public Boolean getAllowPoolSuspension() {
        return allowPoolSuspension;
    }

    @Deprecated
    public Boolean getReadOnly() {
        return readOnly;
    }

    @Deprecated
    public Boolean getRegisterMbeans() {
        return registerMbeans;
    }

    public String getConnectionInitSql() {
        return connectionInitSql;
    }

    public String getTransactionIsolation() {
        return transactionIsolation;
    }

    public Long getValidationTimeout() {
        return validationTimeout;
    }

    public Long getLeakDetectionThreshold() {
        return leakDetectionThreshold;
    }

    public Long getIdleValidationTimeout() {
        return idleValidationTimeout;
    }
}
