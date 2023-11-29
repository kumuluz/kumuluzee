package com.kumuluz.ee.javamail.ri;

import com.kumuluz.ee.common.config.MailServiceConfig;
import com.kumuluz.ee.common.config.MailSessionConfig;
import jakarta.mail.Session;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class MailSessionFactory {

    private final static Logger LOG = Logger.getLogger(MailSessionFactory.class.getSimpleName());

    private final static String TRANSPORT_PROTOCOL_KEY = "mail.transport.protocol";
    private final static String STORE_PROTOCOL_KEY = "mail.store.protocol";

    static Session create(MailSessionConfig cfg) {

        Properties properties = new Properties();

        if (cfg.getDebug() != null) {
            properties.setProperty("mail.debug", cfg.getDebug().toString());
        }

        if (cfg.getTransport() != null) {

            if (cfg.getTransport().getProtocol() != null) {

                String protocol = cfg.getTransport().getProtocol();

                properties.setProperty(TRANSPORT_PROTOCOL_KEY, protocol);

                configureService(properties, cfg.getTransport(), protocol);
            } else {

                LOG.warning("The transport protocol for the Java Mail session is not defined. Please check your configuration.");
            }
        }

        if (cfg.getStore() != null) {

            if (cfg.getStore().getProtocol() != null) {

                String protocol = cfg.getStore().getProtocol();

                properties.setProperty(STORE_PROTOCOL_KEY, protocol);

                configureService(properties, cfg.getStore(), protocol);
            } else {

                LOG.warning("The store protocol for the Java Mail session is not defined. Please check your configuration.");
            }
        }

        if (cfg.getProps() != null && cfg.getProps().size() > 0) {

            for (Map.Entry<String, String> prop : cfg.getProps().entrySet()) {

                properties.setProperty(prop.getKey(), prop.getValue());
            }
        }

        return Session.getInstance(properties, new ManagedPasswordAuthenticator(cfg));
    }

    private static void configureService(Properties properties, MailServiceConfig serviceConfig, String protocol) {

        String prefix = "mail." + protocol;

        if (serviceConfig.getHost() != null) {
            properties.setProperty(prefix + ".host", serviceConfig.getHost());
        }

        if (serviceConfig.getPort() != null) {
            properties.setProperty(prefix + ".port", serviceConfig.getPort().toString());
        }

        if (serviceConfig.getStarttls() != null) {
            properties.setProperty(prefix + ".starttls.enable", serviceConfig.getStarttls().toString());
        }

        if (serviceConfig.getConnectionTimeout() != null) {
            properties.setProperty(prefix + ".connectiontimeout", serviceConfig.getConnectionTimeout().toString());
        }

        if (serviceConfig.getTimeout() != null) {
            properties.setProperty(prefix + ".timeout", serviceConfig.getTimeout().toString());
        }

        if (serviceConfig.getPassword() != null) {

            properties.setProperty(prefix + ".auth", Boolean.TRUE.toString());
        }
    }
}
