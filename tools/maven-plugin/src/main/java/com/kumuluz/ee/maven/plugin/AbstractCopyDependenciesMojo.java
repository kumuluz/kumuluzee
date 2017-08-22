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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * @author Benjamin Kastelic
 * @since 2.4.0
 */
public abstract class AbstractCopyDependenciesMojo extends AbstractMojo {

    private MavenProject mavenProject;
    private MavenSession mavenSession;
    private BuildPluginManager buildPluginManager;

    private String outputDirectory;
    private String baseDirectory;

    public void copyDependencies(MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager buildPluginManager)
            throws MojoExecutionException {

        copyDependencies(mavenProject, mavenSession, buildPluginManager, null);
    }

    public void copyDependencies(MavenProject mavenProject, MavenSession mavenSession,
                                 BuildPluginManager buildPluginManager, String outputSubdirectory)
            throws MojoExecutionException {

        this.mavenProject = mavenProject;
        this.mavenSession = mavenSession;
        this.buildPluginManager = buildPluginManager;

        outputDirectory = mavenProject.getBuild().getOutputDirectory();
        baseDirectory = mavenProject.getBasedir().getAbsolutePath();

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
                executionEnvironment(mavenProject, mavenSession, buildPluginManager)
        );

        copyOrCreateWebapp();
    }

    private void copyOrCreateWebapp() throws MojoExecutionException {

        boolean webappExists = false;

        // search for target/classes/webapp
        Path outputWebApp = Paths.get(outputDirectory, "webapp");

        if (Files.isDirectory(outputWebApp)) {

            webappExists = true;
        }

        // search for src/main/webapp
        if (!webappExists) {

            Path sourceWebApp = Paths.get(baseDirectory, "src", "main", "webapp");

            if (Files.isDirectory(sourceWebApp)) {

                executeMojo(
                        plugin(
                                groupId("org.apache.maven.plugins"),
                                artifactId("maven-resources-plugin"),
                                version(MojoConstants.MAVEN_RESOURCE_PLUGIN_VERSION)
                        ),
                        goal("copy-resources"),
                        configuration(
                                element(name("outputDirectory"), "${basedir}/target/classes/webapp"),
                                element(name("resources"),
                                        element(name("resource"),
                                                element(name("directory"), "src/main/webapp"),
                                                element(name("filtering"), "true")
                                        ))
                        ),
                        executionEnvironment(mavenProject, mavenSession, buildPluginManager)
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

                throw new MojoExecutionException("Could not create the necessary `webapp` directory. Please check the target folder permissions.", e);
            }
        }
    }
}
