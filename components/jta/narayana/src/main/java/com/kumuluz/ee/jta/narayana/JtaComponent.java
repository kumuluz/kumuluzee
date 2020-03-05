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
package com.kumuluz.ee.jta.narayana;

import com.arjuna.ats.arjuna.common.arjPropertyManager;
import com.arjuna.ats.jta.utils.JNDIManager;
import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;
import com.kumuluz.ee.jta.common.JtaProvider;

import javax.naming.NamingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Marcos Koch Salvador
 * @since 2.3.0
 */
@EeComponentDef(name = "Narayana JTA", type = EeComponentType.JTA)
public class JtaComponent implements Component {

    private Logger log = Logger.getLogger(JtaComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {
        if (server.getServer() instanceof ServletServer) {
            ((ServletServer) server.getServer()).registerTransactionManager(JtaProvider.getInstance().getUserTransaction());
        }
    }

    @Override
    public void load() {
        log.info("Initiating Narayana JTA");

        arjPropertyManager.getObjectStoreEnvironmentBean().setObjectStoreDir("ObjectStore");

        try {
            JNDIManager.bindJTAImplementation();
        } catch (NamingException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
