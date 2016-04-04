package com.kumuluz.ee.common.wrapper;

import com.kumuluz.ee.common.dependencies.EeComponentType;

import java.util.List;

/**
 * @author Tilen Faganel
 * @since 2.0.0
 */
public class ComponentDependencyWrapper {

    private EeComponentType type;
    private List<String> implementations;

    public ComponentDependencyWrapper(EeComponentType type, List<String> implementations) {

        this.type = type;
        this.implementations = implementations;
    }

    public EeComponentType getType() {
        return type;
    }

    public void setType(EeComponentType type) {
        this.type = type;
    }

    public List<String> getImplementations() {
        return implementations;
    }

    public void setImplementations(List<String> implementations) {
        this.implementations = implementations;
    }
}
