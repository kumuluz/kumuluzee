/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
*/
package com.kumuluz.ee.maven.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.config.SpecificationConfig;
import com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.util.ApiSpecURLs;
import com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.util.ApiSpecsUtil;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * @author Benjamin Kastelic
 * @since 2.4.0
 */
public abstract class AbstractCopyDependenciesMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected MavenSession session;
    @Component
    protected BuildPluginManager buildPluginManager;
    @Parameter
    List<String> nonFilteredFileExtensions;
    @Parameter
    private String webappDir;
    @Parameter
    private SpecificationConfig specificationConfig;
    private String outputDirectory;
    private String baseDirectory;

    protected void copyDependencies()
            throws MojoExecutionException {

        copyDependencies(null);
        addSwaggerUi();
    }

    /**
     * Copies dependencies to /target/dependency or to target/{outputSubdirectory} if the outputSubdirectory parameter is
     * provided
     */
    protected void copyDependencies(String outputSubdirectory)
            throws MojoExecutionException {

        outputDirectory = project.getBuild().getDirectory();
        baseDirectory = project.getBasedir().getAbsolutePath();

        String outputDirectory = outputSubdirectory == null
                ? this.outputDirectory + "/dependency"
                : this.outputDirectory + "/" + outputSubdirectory;

        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-dependency-plugin"),
                        version(MojoConstants.MAVEN_DEPENDENCY_PLUGIN_VERSION)
                ),
                goal("copy-dependencies"),
                configuration(
                        element("includeScope", "runtime"),
                        element("overWriteSnapshots", "true"),
                        element("excludeArtifactIds", "kumuluzee-loader"),
                        element("outputDirectory", outputDirectory)
                ),
                executionEnvironment(project, session, buildPluginManager)
        );

        copyOrCreateWebapp();
    }

    private void copyOrCreateWebapp() throws MojoExecutionException {

        boolean webappExists = false;

        // search for target/classes/webapp
        Path outputWebApp = Paths.get(outputDirectory, "classes/webapp");

        if (Files.isDirectory(outputWebApp)) {

            webappExists = true;
        }

        // search for src/main/webapp
        if (!webappExists) {

            Path sourceWebApp = webappDir == null ?
                    Paths.get(baseDirectory, "src", "main", "webapp") :
                    Paths.get(baseDirectory, webappDir);

            getLog().info(sourceWebApp.toAbsolutePath().toString());

            if (Files.isDirectory(sourceWebApp)) {

                String sourceWebAppDir = webappDir == null ? "src/main/webapp" : webappDir;

                Xpp3Dom config = new Xpp3Dom(configuration(
                        element(name("outputDirectory"), "${basedir}/target/classes/webapp"),
                        element(name("resources"),
                                element(name("resource"),
                                        element(name("directory"), sourceWebAppDir),
                                        element(name("filtering"), "true")
                                ))
                ));

                if (nonFilteredFileExtensions != null && nonFilteredFileExtensions.size() != 0) {
                    Xpp3Dom nonFilteredFileExtensionsDom = new Xpp3Dom("nonFilteredFileExtensions");
                    for (String nffe : nonFilteredFileExtensions) {

                        Xpp3Dom nonFilteredFileExtensionDom = new Xpp3Dom("nonFilteredFileExtension");
                        nonFilteredFileExtensionDom.setValue(nffe);

                        nonFilteredFileExtensionsDom.addChild(nonFilteredFileExtensionDom);
                    }
                    config.addChild(nonFilteredFileExtensionsDom);
                }

                executeMojo(
                        plugin(
                                groupId("org.apache.maven.plugins"),
                                artifactId("maven-resources-plugin"),
                                version(MojoConstants.MAVEN_RESOURCE_PLUGIN_VERSION)
                        ),
                        goal("copy-resources"),
                        config,
                        executionEnvironment(project, session, buildPluginManager)
                );

                // check if webapp resources were successfully copied
                if (Files.isDirectory(outputWebApp)) {

                    webappExists = true;
                }
            }
        }

        if (!webappExists) {

            try {

                Files.createDirectories(outputWebApp);

            } catch (IOException e) {

                throw new MojoExecutionException("Could not create the necessary `webapp` directory. Please check the target folder " +
                        "permissions.", e);
            }
        }
    }

    /**
     * Download and add SwaggerUi to /target/classes/webapp
     */
    protected void addSwaggerUi()
            throws MojoExecutionException {

        boolean containsApiSpecDependency = false;
        for (Artifact a : project.getDependencyArtifacts()) {
            if ((a.getGroupId().equals("com.kumuluz.ee.swagger") && a.getArtifactId().equals("kumuluzee-swagger")) ||
                    (a.getGroupId().equals("com.kumuluz.ee.openapi") && a.getArtifactId().equals("kumuluzee-openapi"))) {
                containsApiSpecDependency = true;
                break;
            }
        }

        if (containsApiSpecDependency) {

            if (specificationConfig == null) {
                specificationConfig = new SpecificationConfig();
            }

            List<ApiSpecURLs> apiSpecURLs = ApiSpecsUtil.getApiSpecs(project, specificationConfig);

            if (specificationConfig.getIncludeSwaggerUI()) {
                executeMojo(
                        plugin(
                                groupId("com.googlecode.maven-download-plugin"),
                                artifactId("download-maven-plugin"),
                                version(MojoConstants.DOWNLOAD_MAVEN_PLUGIN_VERSION)
                        ),
                        goal("wget"),
                        configuration(
                                element("url", "https://github.com/swagger-api/swagger-ui/archive/v" + MojoConstants.SWAGGER_UI_VERSION +
                                        ".tar.gz"),
                                element("unpack", "true"),
                                element("outputDirectory", project.getBuild().getDirectory())
                        ),
                        executionEnvironment(project, session, buildPluginManager)
                );

                executeMojo(
                        plugin(
                                groupId("org.apache.maven.plugins"),
                                artifactId("maven-resources-plugin"),
                                version(MojoConstants.MAVEN_RESOURCE_PLUGIN_VERSION)
                        ),
                        goal("copy-resources"),
                        configuration(
                                element(name("outputDirectory"), "${basedir}/target/classes/webapp/api-specs/ui"),
                                element(name("resources"),
                                        element(name("resource"),
                                                element(name("directory"), "${project.build.directory}/swagger-ui-" + MojoConstants
                                                        .SWAGGER_UI_VERSION + "/dist")
                                        ))
                        ),
                        executionEnvironment(project, session, buildPluginManager)
                );

                cleanSwaggerUiFromBuildDir();

                ObjectMapper mapper = new ObjectMapper();
                String apiURLs = "";
                try {
                    apiURLs = mapper.writeValueAsString(apiSpecURLs);
                } catch (JsonProcessingException e) {
                    throw new MojoExecutionException("Could not obtain API specifications information from config files.", e);
                }

                executeMojo(
                        plugin(
                                groupId("com.google.code.maven-replacer-plugin"),
                                artifactId("replacer"),
                                version(MojoConstants.REPLACER_PLUGIN_VERSION)
                        ),
                        goal("replace"),
                        configuration(
                                element("ignoreMissingFile", "false"),
                                element("file", project.getBuild().getDirectory() + "/classes/webapp/api-specs/ui/index.html"),
                                element("outputFile", project.getBuild().getDirectory() + "/classes/webapp/api-specs/ui/index.html"),
                                element("regex", "false"),
                                element(name("replacements"),
                                        element(name("replacement"),
                                                element(name("token"), "url: \"http://petstore.swagger.io/v2/swagger.json\""),
                                                element(name("value"), "urls: " + apiURLs))
                                )),
                        executionEnvironment(project, session, buildPluginManager)
                );
            }
        }

    }

    /**
     * Clean up SwaggerUI
     */
    protected void cleanSwaggerUiFromBuildDir()
            throws MojoExecutionException {

        try {

            FileUtils.deleteDirectory(project.getBuild().getDirectory() + "/swagger-ui-" + MojoConstants.SWAGGER_UI_VERSION);

        } catch (IOException e) {

            throw new MojoExecutionException("Could not delete 'swagger-ui' directory. Please check the target folder " +
                    "permissions.", e);
        }
    }
}
