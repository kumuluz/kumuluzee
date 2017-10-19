package com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.config.ApiSpecification;
import com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.config.SpecificationConfig;
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

    public static List<ApiSpecURLs> getApiSpecs(MavenProject project, SpecificationConfig apiSpecificationConfig) throws
            MojoExecutionException {

        List<ApiSpecURLs> apiSpecs = new ArrayList<>();

        List<File> openApiSpecsConfigsLocations = new ArrayList<>();
        String apiSpecsPath = project.getBuild().getDirectory() + "/classes/api-specs";

        File apiSpecsFile = new File(apiSpecsPath);
        getApiSpecPaths(apiSpecsFile, openApiSpecsConfigsLocations);

        String swaggerApiSpecsConfigLocation = project.getBuild().getDirectory() + "/classes/swagger-configuration.json";
        File swaggerConfig = new File(swaggerApiSpecsConfigLocation);

        ObjectMapper mapper = new ObjectMapper();
        if (swaggerConfig.exists()) {
            try {
                JsonNode root = mapper.readTree(swaggerConfig);

                for (JsonNode node : root) {
                    String apiName = node.path("swagger").path("info").path("title").asText() + " - " + node.path("swagger").path("info")
                            .path("version").asText();
                    String apiUrl = "http://" + node.path("swagger").path("host").asText() + "/api-specs/" + node.path("swagger").path
                            ("basePath").asText() +
                            "/swagger.json";

                    ApiSpecURLs specUrl = new ApiSpecURLs();
                    specUrl.setName(apiName);
                    specUrl.setUrl(apiUrl);

                    apiSpecs.add(specUrl);
                }

            } catch (IOException e) {
                throw new MojoExecutionException("Error processing API specification files", e);
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

                            mapper.writeValue(openApiConfig, root);
                        }
                    }

                } catch (IOException e) {
                    throw new MojoExecutionException("Error processing API specification files", e);
                }
            }
        }

        return apiSpecs;
    }

    private static void getApiSpecPaths(File file, List<File> openApiConfigLocations) {

        List<File> content = new ArrayList<>(Arrays.asList(file.listFiles()));

        for (File f : content) {
            if (f.isDirectory()) {
                getApiSpecPaths(f, openApiConfigLocations);
            } else if (f.isFile()) {
                openApiConfigLocations.add(f);
            }
        }
    }
}
