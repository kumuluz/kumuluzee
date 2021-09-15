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

import java.util.List;

public class GzipConfig {

    public static class Builder {

        private Boolean enabled = false;
        private Integer minGzipSize;
        private List<String> includedMethods;
        private List<String> includedMimeTypes;
        private List<String> excludedMimeTypes;
        private List<String> excludedAgentPatterns;
        private List<String> excludedPaths;
        private List<String> includedPaths;

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder minGzipSize(Integer minGzipSize) {
            this.minGzipSize = minGzipSize;
            return this;
        }

        public Builder includedMethods(List<String> includedMethods) {
            this.includedMethods = includedMethods;
            return this;
        }

        public Builder includedMimeTypes(List<String> includedMimeTypes) {
            this.includedMimeTypes = includedMimeTypes;
            return this;
        }

        public Builder excludedMimeTypes(List<String> excludedMimeTypes) {
            this.excludedMimeTypes = excludedMimeTypes;
            return this;
        }

        public Builder excludedAgentPatterns(List<String> excludedAgentPatterns) {
            this.excludedAgentPatterns = excludedAgentPatterns;
            return this;
        }

        public Builder excludedPaths(List<String> excludedPaths) {
            this.excludedPaths = excludedPaths;
            return this;
        }

        public Builder includedPaths(List<String> includedPaths) {
            this.includedPaths = includedPaths;
            return this;
        }

        public GzipConfig build() {

            GzipConfig gzipConfig = new GzipConfig();
            gzipConfig.enabled = enabled;
            gzipConfig.minGzipSize = minGzipSize;
            gzipConfig.includedMethods = includedMethods;
            gzipConfig.includedMimeTypes = includedMimeTypes;
            gzipConfig.excludedMimeTypes = excludedMimeTypes;
            gzipConfig.excludedAgentPatterns = excludedAgentPatterns;
            gzipConfig.excludedPaths = excludedPaths;
            gzipConfig.includedPaths = includedPaths;

            return gzipConfig;
        }
    }

    private Boolean enabled;
    private Integer minGzipSize;
    private List<String> includedMethods;
    private List<String> includedMimeTypes;
    private List<String> excludedMimeTypes;
    private List<String> excludedAgentPatterns;
    private List<String> excludedPaths;
    private List<String> includedPaths;

    private GzipConfig() {
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getMinGzipSize() {
        return minGzipSize;
    }

    public void setMinGzipSize(Integer minGzipSize) {
        this.minGzipSize = minGzipSize;
    }

    public List<String> getIncludedMethods() {
        return includedMethods;
    }

    public void setIncludedMethods(List<String> includedMethods) {
        this.includedMethods = includedMethods;
    }

    public List<String> getIncludedMimeTypes() {
        return includedMimeTypes;
    }

    public void setIncludedMimeTypes(List<String> includedMimeTypes) {
        this.includedMimeTypes = includedMimeTypes;
    }

    public List<String> getExcludedMimeTypes() {
        return excludedMimeTypes;
    }

    public void setExcludedMimeTypes(List<String> excludedMimeTypes) {
        this.excludedMimeTypes = excludedMimeTypes;
    }

    public List<String> getExcludedPaths() {
        return excludedPaths;
    }

    public void setExcludedPaths(List<String> excludedPaths) {
        this.excludedPaths = excludedPaths;
    }

    public List<String> getIncludedPaths() {
        return includedPaths;
    }

    public void setIncludedPaths(List<String> includedPaths) {
        this.includedPaths = includedPaths;
    }
}
