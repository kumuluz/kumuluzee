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
public class EeComponentWrapper extends ComponentWrapper {

    private EeComponentType type;

    public EeComponentWrapper(Component component, String name,
                              EeComponentDependency[] dependencies,
                              EeComponentOptional[] optionalDependencies) {

        super(component, name, dependencies, optionalDependencies);
    }

    public EeComponentWrapper(Component component, String name, EeComponentType type,
                              EeComponentDependency[] dependencies,
                              EeComponentOptional[] optionalDependencies) {

        super(component, name, dependencies, optionalDependencies);
        this.type = type;
    }

    public EeComponentType getType() {
        return type;
    }

    public void setType(EeComponentType type) {
        this.type = type;
    }
}
