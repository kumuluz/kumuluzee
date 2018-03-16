package com.kumuluz.ee.common.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MailSessionConfig {

    public static class Builder {

        private String jndiName;
        private Boolean debug;

        private MailServiceConfig.Builder transport;
        private MailServiceConfig.Builder store;

        private Map<String, String> props = new HashMap<>();

        public Builder jndiName(String jndiName) {
            this.jndiName = jndiName;
            return this;
        }

        public Builder debug(Boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder transport(MailServiceConfig.Builder transport) {
            this.transport = transport;
            return this;
        }

        public Builder store(MailServiceConfig.Builder store) {
            this.store = store;
            return this;
        }

        public Builder prop(String key, String value) {
            this.props.put(key, value);
            return this;
        }

        public MailSessionConfig build() {

            MailSessionConfig mailSessionConfig = new MailSessionConfig();
            mailSessionConfig.jndiName = jndiName;
            mailSessionConfig.debug = debug;

            if (transport != null) mailSessionConfig.transport = transport.build();
            if (store != null)  mailSessionConfig.store = store.build();

            mailSessionConfig.props = Collections.unmodifiableMap(props);

            return mailSessionConfig;
        }
    }

    private String jndiName;
    private Boolean debug;

    private MailServiceConfig transport;
    private MailServiceConfig store;

    private Map<String, String> props;

    private MailSessionConfig() {

    }

    public String getJndiName() {
        return jndiName;
    }

    public Boolean getDebug() {
        return debug;
    }

    public MailServiceConfig getTransport() {
        return transport;
    }

    public MailServiceConfig getStore() {
        return store;
    }

    public Map<String, String> getProps() {
        return props;
    }
}
