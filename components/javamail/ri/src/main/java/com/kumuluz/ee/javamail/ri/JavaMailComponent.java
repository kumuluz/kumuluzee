package com.kumuluz.ee.javamail.ri;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.config.MailSessionConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;

import javax.mail.Session;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.6.0
 */
@EeComponentDef(name = "JavaMailRI", type = EeComponentType.MAIL)
public class JavaMailComponent implements Component {

    private Logger log = Logger.getLogger(JavaMailComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {

        if (eeConfig.getMailSessions() != null && eeConfig.getMailSessions().size() > 0
                && server.getServer() instanceof ServletServer) {

            ServletServer servletServer = (ServletServer) server.getServer();

            for (MailSessionConfig cfg : eeConfig.getMailSessions()) {

                Session session = MailSessionFactory.create(cfg);

                servletServer.registerResource(session, cfg.getJndiName());
            }
        }
    }

    @Override
    public void load() {

        log.info("Initiating JavaMailRi");
    }
}
