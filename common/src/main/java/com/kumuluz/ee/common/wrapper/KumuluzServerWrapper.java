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
package com.kumuluz.ee.common.wrapper;

import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.dependencies.EeComponentType;

import java.util.List;

/**
 * @author Tilen Faganel
 * @since 2.0.0
 */
public class KumuluzServerWrapper {

    private KumuluzServer server;
    private String name;
    private EeComponentType[] providedEeComponents;

    public KumuluzServerWrapper(KumuluzServer server, String name, EeComponentType[] providedEeComponents) {

        this.server = server;
        this.name = name;
        this.providedEeComponents = providedEeComponents;
    }

    public KumuluzServer getServer() {
        return server;
    }

    public void setServer(KumuluzServer server) {
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EeComponentType[] getProvidedEeComponents() {
        return providedEeComponents;
    }

    public void setProvidedEeComponents(EeComponentType[] providedEeComponents) {
        this.providedEeComponents = providedEeComponents;
    }
}
