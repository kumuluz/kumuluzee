package com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.config;

import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration for specific ApiSpecification.
 *
 * @author Zvone Gazvoda
 * @since 2.5.0
 */
public class ApiSpecification {

    @Parameter
    private String applicationPath;
    @Parameter
    private String resourcePackages;

    public String getApplicationPath() {
        return applicationPath;
    }

    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    public Set<String> getResourcePackages() {
        Set<String> rp = new HashSet(Arrays.asList(StringUtils.split(resourcePackages, ",")));
        return rp;
    }

    public void setResourcePackages(String resourcePackages) {
        this.resourcePackages = resourcePackages;
    }

    public void fromExternalConfig(Xpp3Dom dom) {
        for (Xpp3Dom child : dom.getChildren()) {
            String name = child.getName();

            switch (name) {
                case "applicationPath":
                    applicationPath = child.getValue();
                    break;
                case "resourcePackages":
                    resourcePackages = child.getValue();
                    break;
            }
        }
    }
}
