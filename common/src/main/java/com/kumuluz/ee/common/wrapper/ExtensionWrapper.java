package com.kumuluz.ee.common.wrapper;

import com.kumuluz.ee.common.Extension;
import com.kumuluz.ee.common.dependencies.EeComponentDependency;
import com.kumuluz.ee.common.dependencies.EeComponentOptional;

public class ExtensionWrapper<T extends Extension> {

    private T extension;
    private String name;
    private String group;
    private EeComponentDependency[] dependencies;
    private EeComponentOptional[] optionalDependencies;

    public ExtensionWrapper(T extension, String name, String group, EeComponentDependency[] dependencies, EeComponentOptional[] optionalDependencies) {
        this.extension = extension;
        this.name = name;
        this.group = group;
        this.dependencies = dependencies;
        this.optionalDependencies = optionalDependencies;
    }

    public T getExtension() {
        return extension;
    }

    public void setExtension(T extension) {
        this.extension = extension;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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
}
