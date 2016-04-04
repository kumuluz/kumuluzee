package com.kumuluz.ee.common.wrapper;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.dependencies.EeComponentType;

import java.util.List;

/**
 * @author Tilen Faganel
 * @since 2.0.0
 */
public class ComponentWrapper {

    private Component component;
    private EeComponentType type;
    private List<ComponentDependencyWrapper> dependencies;

    public ComponentWrapper(Component component, EeComponentType type, List<ComponentDependencyWrapper> dependencies) {

        this.component = component;
        this.type = type;
        this.dependencies = dependencies;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public EeComponentType getType() {
        return type;
    }

    public void setType(EeComponentType type) {
        this.type = type;
    }

    public List<ComponentDependencyWrapper> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<ComponentDependencyWrapper> dependencies) {
        this.dependencies = dependencies;
    }
}
