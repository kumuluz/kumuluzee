package com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.config.ApiSpecification;
import com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.config.SpecificationConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * ApiSpecsUtil class.
 *
 * @author Zvone Gazvoda
 * @since 2.5.0
 */
public class ApiSpecsUtil {

    private static final Logger LOG = Logger.getLogger(ApiSpecsUtil.class.getName());

    public static List<ApiSpecURLs> getApiSpecs(MavenProject project, SpecificationConfig apiSpecificationConfig) throws
            MojoExecutionException {

        List<ApiSpecURLs> apiSpecs = new ArrayList<>();

        List<File> openApiSpecsConfigsLocations = new ArrayList<>();
        List<File> swaggerSpecsConfigsLocations = new ArrayList<>();

        String apiSpecsPath = project.getBuild().getDirectory() + "/classes/api-specs";

        File apiSpecFiles = new File(apiSpecsPath);
        getApiSpecPaths(apiSpecFiles, openApiSpecsConfigsLocations, ApiSpecType.OPENAPI);

        getApiSpecPaths(apiSpecFiles, swaggerSpecsConfigsLocations, ApiSpecType.SWAGGER);

        ObjectMapper mapper = new ObjectMapper();
        if (swaggerSpecsConfigsLocations.size() > 0) {
            for (File swaggerConfig : swaggerSpecsConfigsLocations) {
                try {
                    JsonNode root = mapper.readTree(swaggerConfig);

                    String apiName = root.path("swagger").path("info").path("title").asText() + " - " + root.path("swagger").path
                            ("info").path("version").asText();

                    String basePath = root.path("swagger").path("basePath").asText();
                    basePath = StringUtils.strip(basePath, "/");

                    String apiUrl = "http://" + root.path("swagger").path("host").asText() + "/api-specs/" + basePath +
                            "/swagger.json";

                    ApiSpecURLs specUrl = new ApiSpecURLs();
                    specUrl.setName(apiName);
                    specUrl.setUrl(apiUrl);

                    apiSpecs.add(specUrl);

                } catch (IOException e) {
                    throw new MojoExecutionException("Error processing API specification files", e);
                }
            }
        } else if (openApiSpecsConfigsLocations.size() > 0) {
            for (File openApiConfig : openApiSpecsConfigsLocations) {

                try {
                    JsonNode root = mapper.readTree(openApiConfig);

                    String apiName = root.path("openAPI").path("info").path("title").asText() + " - " + root.path("openAPI").path
                            ("info").path("version").asText();

                    URL url = null;
                    for (JsonNode node : root.path("openAPI").path("servers")) {
                        url = new URL(node.path("url").asText());
                        break;
                    }

                    if (url != null) {
                        String apiUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/api-specs" + url.getPath() +
                                "/openapi.json";

                        ApiSpecURLs specUrl = new ApiSpecURLs();
                        specUrl.setName(apiName);
                        specUrl.setUrl(apiUrl);

                        apiSpecs.add(specUrl);

                        // Update resourcePackages
                        String applicationPathUrl = url.getPath();
                        Optional<ApiSpecification> config = apiSpecificationConfig.getApiSpecifications().stream().
                                filter(as -> applicationPathUrl
                                        .equals(as.getApplicationPath())).findFirst();
                        if (config.isPresent()) {

                            ArrayNode array = mapper.valueToTree(config.get().getResourcePackages());
                            ((ObjectNode) root).putArray("resourcePackages").addAll(array);

                            mapper.enable(SerializationFeature.INDENT_OUTPUT).writeValue(openApiConfig, root);
                        }
                    }

                } catch (IOException e) {
                    throw new MojoExecutionException("Error processing API specification files", e);
                }
            }
        }

        return apiSpecs;
    }

    private static void getApiSpecPaths(File file, List<File> openApiConfigLocations, ApiSpecType type) {

        File[] content = file.listFiles();

        if (content != null) {
            for (File f : content) {
                if (f.isDirectory()) {
                    getApiSpecPaths(f, openApiConfigLocations, type);
                } else if (f.getName().equals(type.toString())) {
                    openApiConfigLocations.add(f);
                }
            }
        }
    }
}
