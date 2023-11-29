package com.kumuluz.ee.javamail.ri;

import com.kumuluz.ee.common.config.MailServiceConfig;
import com.kumuluz.ee.common.config.MailSessionConfig;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

public class ManagedPasswordAuthenticator extends Authenticator {

    private MailSessionConfig mailSessionConfig;

    ManagedPasswordAuthenticator(MailSessionConfig mailSessionConfig) {
        this.mailSessionConfig = mailSessionConfig;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {

        String protocol = getRequestingProtocol();

        MailServiceConfig requestingConfig = null;

        if (mailSessionConfig.getTransport() != null && protocol.equals(mailSessionConfig.getTransport().getProtocol())) {
            requestingConfig = mailSessionConfig.getTransport();
        } else if (mailSessionConfig.getStore() != null && protocol.equals(mailSessionConfig.getStore().getProtocol())) {
            requestingConfig = mailSessionConfig.getStore();
        }

        if (requestingConfig != null && requestingConfig.getPassword() != null) {

            return new PasswordAuthentication(requestingConfig.getUsername(), requestingConfig.getPassword());
        }

        return null;
    }
}
