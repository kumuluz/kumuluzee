package com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.config.ApiSpecURLs;

import java.net.URL;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zvoneg on 09/10/2017.
 */
public class ApiSpecsUtil {

    public static List<ApiSpecURLs> getApiSpecs(MavenProject project) throws MojoExecutionException {

        List<ApiSpecURLs> apiSpecs = new ArrayList<>();

        String swaggerApiSpecsConfigLocation = project.getBuild().getDirectory() + "/classes/swagger-configuration.json";
        String openApiSpecsConfigLocation = project.getBuild().getDirectory() + "/classes/openapi-configuration.json";

        File swaggerConfig = new File(swaggerApiSpecsConfigLocation);
        File openApiConfig = new File(openApiSpecsConfigLocation);

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
        } else if (openApiConfig.exists()) {
            try {
                JsonNode root = mapper.readTree(openApiConfig);

                String apiName = root.path("openAPI").path("info").path("title").asText() + " - " + root.path("openAPI").path("info")
                        .path("version").asText();

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
                }

            } catch (IOException e) {
                throw new MojoExecutionException("Error processing API specification files", e);
            }
        }

        return apiSpecs;
    }
}
