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
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * @author Benjamin Kastelic
 */
public abstract class AbstractPackageMojo extends AbstractCopyDependenciesAndWebappMojo {

    private static final String LOADER_JAR = "META-INF/loader/kumuluzee-loader.jar";
    private static final String TEMP_DIR_NAME_PREFIX = "kumuluzee-loader.";
    private static final String CLASS_SUFFIX = ".class";

    private MavenProject mavenProject;
    private MavenSession mavenSession;
    private BuildPluginManager buildPluginManager;

    private String outputDirectory;
    private String finalName;

    public void repackage(MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager buildPluginManager)
            throws MojoExecutionException {
        this.mavenProject = mavenProject;
        this.mavenSession = mavenSession;
        this.buildPluginManager = buildPluginManager;

        outputDirectory = mavenProject.getBuild().getDirectory();
        finalName = mavenProject.getBuild().getFinalName();

        copyDependencies();
        unpackDependencies();
        packageJar();
        renameJars();
    }

    private void copyDependencies() throws MojoExecutionException {
        super.copyDependencies(mavenProject, mavenSession, buildPluginManager, "classes/lib");
    }

//    private void unpackDependencies() throws MojoExecutionException {
//        ExecutionEnvironment executionEnvironment = executionEnvironment(mavenProject, mavenSession, buildPluginManager);
//
//        try {
//            executeMojo(
//                    plugin(
//                            groupId("org.apache.maven.plugins"),
//                            artifactId("maven-dependency-plugin"),
//                            version("3.0.1")
//                    ),
//                    goal("unpack"),
//                    configuration(
//                            element("artifact", LOADER_JAR_GAV),
//                            element("excludes", "META-INF/**"),
//                            element("outputDirectory", "${project.build.directory}/classes")
//                    ),
//                    executionEnvironment
//            );
//        } catch (MojoExecutionException e) {
//            unpackDependenciesFallback();
//        }
//    }

    private void unpackDependencies() throws MojoExecutionException {
        getLog().info("Unpacking kumuluzee-loader dependency.");

        try {
            // get plugin JAR
            String pluginJarPath = getPluginJarPath();
            JarFile pluginJar = new JarFile(new File(pluginJarPath));

            // extract loader JAR from plugin JAR
            JarEntry pluginJarloaderJarEntry = pluginJar.getJarEntry(LOADER_JAR);
            InputStream loaderJarInputStream = pluginJar.getInputStream(pluginJarloaderJarEntry);

            File tmpDirectory = new File(System.getProperty("java.io.tmpdir"), "EeBootLoader");
            if (!tmpDirectory.exists()) {
                tmpDirectory.mkdir();
            }
            chmod777(tmpDirectory);

            File tmpLoaderJarFile = File.createTempFile(TEMP_DIR_NAME_PREFIX, null, tmpDirectory);
            tmpLoaderJarFile.deleteOnExit();
            chmod777(tmpLoaderJarFile);

            FileUtils.copyInputStreamToFile(loaderJarInputStream, tmpLoaderJarFile);

            // extract loader JAR contents
            JarFile loaderJar = new JarFile(tmpLoaderJarFile);
            loaderJar
                    .stream()
                    .parallel()
                    .filter(loaderJarEntry -> loaderJarEntry.getName().toLowerCase().endsWith(CLASS_SUFFIX))
                    .forEach(loaderJarEntry -> {
                        try {
                            File file = new File(outputDirectory, "classes" + File.separator + loaderJarEntry.getName());
                            if (file.getParentFile() != null) {
                                file.getParentFile().mkdirs();
                            }

                            InputStream inputStream = loaderJar.getInputStream(loaderJarEntry);
                            FileUtils.copyInputStreamToFile(inputStream, file);
                        } catch (IOException e) {
                            // ignore
                        }
                    });

            loaderJar.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to unpack kumuluzee-loader dependency: " + e.getMessage() + ".");
        }
    }

    private String getPluginJarPath() throws MojoExecutionException {
        try {
            ProtectionDomain protectionDomain = RepackageMojo.class.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            URI location = codeSource == null ? null : codeSource.getLocation().toURI();
            return location == null ? null : location.getSchemeSpecificPart();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to retrieve plugin JAR file path.");
        }
    }

    private void chmod777(File file) {
        file.setReadable(true, false);
        file.setWritable(true, false);
        file.setExecutable(true, false);
    }

    private void packageJar() throws MojoExecutionException {
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-jar-plugin"),
                        version("3.0.2")
                ),
                goal("jar"),
                configuration(
                        element("finalName", finalName),
                        element("outputDirectory", outputDirectory),
                        element("classifier", "uber"),
                        element("forceCreation", "true"),
                        element("archive",
                                element("manifest",
                                        element("mainClass", "com.kumuluz.ee.loader.EeBootLoader")
                                )
                        )
                ),
                executionEnvironment(mavenProject, mavenSession, buildPluginManager)
        );
    }

    private void renameJars() throws MojoExecutionException {
        try {
            File sourceFile1 = new File(outputDirectory, finalName + ".jar");
            if (sourceFile1.exists()) {
                Files.move(
                        sourceFile1.toPath(),
                        sourceFile1.toPath().resolveSibling(finalName + ".jar.original"),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            File sourceFile2 = new File(outputDirectory, finalName + "-uber.jar");
            if (sourceFile2.exists()) {
                Files.move(
                        sourceFile2.toPath(),
                        sourceFile2.toPath().resolveSibling(finalName + ".jar"),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to rename final build artifact.");
        }
    }
}
