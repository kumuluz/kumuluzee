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
        private Long connectionTimeout = 30000L;
        private Long idleTimeout = 600000L;
        private Long maxLifetime = 1800000L;
        private Integer minIdle;
        private Integer maxSize = 10;
        private String name;
        private Long initializationFailTimeout = 1L;
        private Boolean isolateInternalQueries = false;
        private Boolean allowPoolSuspension = false;
        private Boolean readOnly = false;
        private Boolean registerMbeans = false;
        private String connectionInitSql;
        private String transactionIsolation;
        private Long validationTimeout = 5000L;
        private Long leakDetectionThreshold = 0L;

        public Builder autoCommit(Boolean autoCommit) {
            this.autoCommit = autoCommit;
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

        public Builder minIdle(Integer minIdle) {
            this.minIdle = minIdle;
            return this;
        }

        public Builder maxSize(Integer maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder initializationFailTimeout(Long initializationFailTimeout) {
            this.initializationFailTimeout = initializationFailTimeout;
            return this;
        }

        public Builder isolateInternalQueries(Boolean isolateInternalQueries) {
            this.isolateInternalQueries = isolateInternalQueries;
            return this;
        }

        public Builder allowPoolSuspension(Boolean allowPoolSuspension) {
            this.allowPoolSuspension = allowPoolSuspension;
            return this;
        }

        public Builder readOnly(Boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

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

        public DataSourcePoolConfig build() {

            DataSourcePoolConfig dataSourcePoolConfig = new DataSourcePoolConfig();
            dataSourcePoolConfig.autoCommit = autoCommit;
            dataSourcePoolConfig.connectionTimeout = connectionTimeout;
            dataSourcePoolConfig.idleTimeout = idleTimeout;
            dataSourcePoolConfig.maxLifetime = maxLifetime;
            dataSourcePoolConfig.minIdle = minIdle;
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

            return dataSourcePoolConfig;
        }
    }

    private Boolean autoCommit;
    private Long connectionTimeout;
    private Long idleTimeout;
    private Long maxLifetime;
    private Integer minIdle;
    private Integer maxSize;
    private String name;
    private Long initializationFailTimeout;
    private Boolean isolateInternalQueries;
    private Boolean allowPoolSuspension;
    private Boolean readOnly;
    private Boolean registerMbeans;
    private String connectionInitSql;
    private String transactionIsolation;
    private Long validationTimeout;
    private Long leakDetectionThreshold;

    private DataSourcePoolConfig() {
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

    public Integer getMinIdle() {
        return minIdle;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public String getName() {
        return name;
    }

    public Long getInitializationFailTimeout() {
        return initializationFailTimeout;
    }

    public Boolean getIsolateInternalQueries() {
        return isolateInternalQueries;
    }

    public Boolean getAllowPoolSuspension() {
        return allowPoolSuspension;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

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
}
