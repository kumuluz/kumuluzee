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
