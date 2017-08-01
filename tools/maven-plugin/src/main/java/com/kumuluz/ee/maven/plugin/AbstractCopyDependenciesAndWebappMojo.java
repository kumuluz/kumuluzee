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

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * @author Benjamin Kastelic
 */
public abstract class AbstractCopyDependenciesAndWebappMojo extends AbstractMojo {

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

        outputDirectory = mavenProject.getBuild().getDirectory();
        baseDirectory = mavenProject.getBasedir().getAbsolutePath();

        String outputDirectory = outputSubdirectory == null
                ? this.outputDirectory + "/dependency"
                : this.outputDirectory + "/" + outputSubdirectory;

        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-dependency-plugin"),

                        version("3.0.1")
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
        final String webXmlContent =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<web-app xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd\"\n" +
                "         version=\"3.1\" xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\">\n" +
                "</web-app>";

        boolean webappExists = false;
        boolean webappEmpty = false;

        // search for target/classes/webapp
        File targetWebapp = new File(outputDirectory + "/classes/webapp");
        if (targetWebapp.isDirectory() && targetWebapp.exists()) {
            webappExists = true;

            if (targetWebapp.list().length <= 0) {
                webappEmpty = true;
            }
        }

        // search for src/main/webapp
        if (!webappExists) {
            File sourceWebapp = new File(baseDirectory + "/src/main/webapp");
            if (sourceWebapp.isDirectory() && sourceWebapp.exists()) {
                webappExists = true;

                if (sourceWebapp.list().length <= 0) {
                    webappEmpty = true;
                }
            }

            if (webappExists) {
                executeMojo(
                        plugin(
                                groupId("org.apache.maven.plugins"),
                                artifactId("maven-resources-plugin"),
                                version("3.0.2")
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
            }

            // check if webapp resources were sucessfully copied
            targetWebapp = new File(outputDirectory + "/classes/webapp");
            if (targetWebapp.isDirectory() && targetWebapp.exists()) {
                webappExists = true;

                if (targetWebapp.list().length <= 0) {
                    webappEmpty = true;
                }
            }
        }

        if (!webappExists || webappEmpty) {
            try {
                File targetWebXml = new File(outputDirectory + "/classes/webapp/WEB-INF/web.xml");
                targetWebXml.getParentFile().mkdirs();
                targetWebXml.createNewFile();
                FileUtils.writeStringToFile(targetWebXml, webXmlContent, "UTF-8");
            } catch (Exception e) {
                // Ignore ...
            }
        }
    }
}
