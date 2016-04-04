package com.kumuluz.ee.common.wrapper;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.dependencies.EeComponentDependency;
import com.kumuluz.ee.common.dependencies.EeComponentOptional;
import com.kumuluz.ee.common.dependencies.EeComponentType;

import java.util.List;

/**
 * @author Tilen Faganel
 * @since 2.0.0
 */
public class ComponentWrapper {

    private Component component;
    private String name;
    private EeComponentDependency[] dependencies;
    private EeComponentOptional[] optionalDependencies;


    public ComponentWrapper(Component component, String name,
                            EeComponentDependency[] dependencies,
                            EeComponentOptional[] optionalDependencies) {
        this.component = component;
        this.name = name;
        this.dependencies = dependencies;
        this.optionalDependencies = optionalDependencies;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }


    public EeComponentDependency[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(EeComponentDependency[] dependencies) {
        this.dependencies = dependencies;
    }

    public EeComponentOptional[] getOptionalDependencies() {
        return optionalDependencies;
    }

    public void setOptionalDependencies(EeComponentOptional[] optionalDependencies) {
        this.optionalDependencies = optionalDependencies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
