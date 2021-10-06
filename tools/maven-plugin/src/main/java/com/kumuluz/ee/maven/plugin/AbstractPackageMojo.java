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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * @author Benjamin Kastelic
 * @since 2.4.0
 */
public abstract class AbstractPackageMojo extends AbstractCopyDependenciesMojo {

    private static final String LOADER_JAR = "META-INF/loader/kumuluzee-loader.jar";
    private static final String TEMP_DIR_NAME_PREFIX = "kumuluzee-loader";
    private static final String CLASS_SUFFIX = ".class";

    @Parameter(defaultValue = "com.kumuluz.ee.EeApplication")
    private String mainClass;

    private String buildDirectory;
    private String outputDirectory;
    private String finalName;

    protected void repackage() throws MojoExecutionException {
        buildDirectory = project.getBuild().getDirectory();
        outputDirectory = project.getBuild().getOutputDirectory();
        finalName = project.getBuild().getFinalName();

        checkPrecoditions();
        copyDependencies("classes/lib");
        unpackDependencies();
        packageJar();
        renameJars();
    }

    private void checkPrecoditions() throws MojoExecutionException {
        getLog().info("Checking if project meets the preconditions.");

        // only jar packagins if allowed
        if (!project.getPackaging().toLowerCase().equals("jar")) {
            throw new MojoExecutionException("Only projects of \"jar\" packaging can be repackaged into an Uber JAR.");
        }
    }

    private void unpackDependencies() throws MojoExecutionException {
        getLog().info("Unpacking kumuluzee-loader dependency.");

        try {
            // get plugin JAR
            URI pluginJarURI = getPluginJarPath();

            Path pluginJarFile = Paths.get(pluginJarURI);

            // explicit cast to ClassLoader null is required for Java 15 compilation
            FileSystem pluginJarFs = FileSystems.newFileSystem(pluginJarFile, (ClassLoader) null);

            Path loaderJarFile = pluginJarFs.getPath(LOADER_JAR);
            Path tmpJar = Files.createTempFile(TEMP_DIR_NAME_PREFIX, ".tmp");

            Files.copy(loaderJarFile, tmpJar, StandardCopyOption.REPLACE_EXISTING);

            JarFile loaderJar = new JarFile(tmpJar.toFile(), true, ZipFile.OPEN_READ, JarFile.runtimeVersion());

            loaderJar.stream().parallel()
                    .filter(loaderJarEntry -> loaderJarEntry.getName().toLowerCase().endsWith(CLASS_SUFFIX))
                    .forEach(loaderJarEntry -> {
                        try {

                            Path outputPath = Paths.get(outputDirectory, loaderJarEntry.getName());

                            Path outputPathParent = outputPath.getParent();

                            if (outputPathParent != null) {

                                Files.createDirectories(outputPathParent);
                            }

                            InputStream inputStream = loaderJar.getInputStream(loaderJarEntry);

                            Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);

                            inputStream.close();
                        } catch (IOException ignored) {
                        }
                    });

            loaderJar.close();

            Files.delete(tmpJar);

            // Create the boot loader config file
            Path loaderConf = Paths.get(outputDirectory, "META-INF", "kumuluzee", "boot-loader.properties");

            Path loaderConfParent = loaderConf.getParent();

            if (!Files.exists(loaderConfParent)) {

                Files.createDirectories(loaderConfParent);
            }

            String loaderConfContent = "main-class=" + mainClass;

            Files.write(loaderConf, loaderConfContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to unpack kumuluzee-loader dependency: " + e.getMessage() + ".");
        }
    }

    private URI getPluginJarPath() throws MojoExecutionException {
        try {
            ProtectionDomain protectionDomain = RepackageMojo.class.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();

            if (codeSource == null) {
                throw new MojoExecutionException("Failed to retrieve plugin JAR file path. Unobtainable Code Source.");
            }

            return codeSource.getLocation().toURI();
        } catch (URISyntaxException e) {
            throw new MojoExecutionException("Failed to retrieve plugin JAR file path.", e);
        }
    }

    private void packageJar() throws MojoExecutionException {

        // Execute maven-jar-plugin in a separate execution environment, in order not to pollute the current project with
        // attached artifact. Attached artifact will replace the current main artifact after rename, but reference in
        // project.getAttachedArtifacts() cannot be removed after it's been added by maven-jar-plugin.
        MavenProject projectClone = new MavenProject(project);
        MavenSession sessionClone = session.clone();
        sessionClone.setCurrentProject(projectClone);

        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-jar-plugin"),
                        version(MojoConstants.MAVEN_JAR_PLUGIN_VERSION)
                ),
                goal("jar"),
                configuration(
                        element("finalName", finalName),
                        element("outputDirectory", buildDirectory),
                        element("classifier", "uber"),
                        element("forceCreation", "true"),
                        element("archive",
                                element("manifest",
                                        element("mainClass", "com.kumuluz.ee.loader.EeBootLoader")
                                )
                        )
                ),
                executionEnvironment(projectClone, sessionClone, buildPluginManager)
        );
    }

    private void renameJars() throws MojoExecutionException {
        try {
            Path sourcePath1 = Paths.get(buildDirectory, finalName + ".jar");

            getLog().info("Repackaging jar: " + sourcePath1.toAbsolutePath());

            if (Files.exists(sourcePath1)) {
                Files.move(
                        sourcePath1,
                        sourcePath1.resolveSibling(finalName + ".jar.original"),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            Path sourcePath2 = Paths.get(buildDirectory, finalName + "-uber.jar");

            if (Files.exists(sourcePath2)) {
                Files.move(
                        sourcePath2,
                        sourcePath2.resolveSibling(finalName + ".jar"),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to rename the final build artifact.");
        }
    }
}
