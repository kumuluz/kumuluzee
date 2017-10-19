package com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.config;

import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for Api Specifications.
 *
 * @author Zvone Gazvoda
 * @since 2.5.0
 */
public class SpecificationConfig {

    @Parameter
    private List<ApiSpecification> apiSpecifications;

    @Parameter(defaultValue = "false")
    private boolean includeSwaggerUI;

    public List<ApiSpecification> getApiSpecifications() {
        if (apiSpecifications == null) {
            apiSpecifications = new ArrayList<>();
        }

        return apiSpecifications;
    }

    public void setApiSpecifications(List<ApiSpecification> apiSpecifications) {
        this.apiSpecifications = apiSpecifications;
    }

    public boolean getIncludeSwaggerUI() {
        return includeSwaggerUI;
    }

    public void setIncludeSwaggerUI(boolean includeSwaggerUI) {
        this.includeSwaggerUI = includeSwaggerUI;
    }

    public void fromExternalConfig(Xpp3Dom dom) {
        for (Xpp3Dom child : dom.getChildren()) {
            String name = child.getName();

            if (name.equals("apiSpecifications")) {
                apiSpecifications = new ArrayList<>();

                for (Xpp3Dom cchild : child.getChildren()) {
                    ApiSpecification apiSpecification = new ApiSpecification();
                    apiSpecification.fromExternalConfig(cchild);

                    apiSpecifications.add(apiSpecification);
                }
            } else if (name.equals("includeSwaggerUI")) {
                includeSwaggerUI = Boolean.valueOf(child.getValue());
            }
        }
    }
}
