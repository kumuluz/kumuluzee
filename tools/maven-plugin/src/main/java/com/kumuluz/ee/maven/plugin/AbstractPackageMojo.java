package com.kumuluz.ee.maven.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
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

public abstract class AbstractPackageMojo extends AbstractCopyDependenciesAndWebappMojo {

    private static final String LOADER_JAR = "META-INF/loader/kumuluzee-loader.jar";
    //    private static final String LOADER_JAR_GAV = "com.kumuluz.ee:kumuluzee-loader:2.3.0-SNAPSHOT";
    private static final String TEMP_DIR_NAME_PREFIX = "kumuluzee-loader.";
    private static final String CLASS_SUFFIX = ".class";

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager buildPluginManager;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private String outputDirectory;

    @Parameter(defaultValue = "${project.build.finalName}", required = true)
    private String finalName;

    public void repackage(/*MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager buildPluginManager*/)
            throws MojoExecutionException {
        if (mavenProject.getPackaging().equals("pom")) {
            getLog().debug("Package \"goal\" could not be applied to \"pom\" mavenProject.");
            return;
        }

        this.mavenProject = mavenProject;
        this.mavenSession = mavenSession;
        this.buildPluginManager = buildPluginManager;

        copyDependencies1();
        unpackDependencies();
        packageJar();
        renameJars();
    }

    private void copyDependencies1() throws MojoExecutionException {
        String OUTPUT_SUBDIRECTORY_FORMAT = "%s%s%s";
        String outputSubdirectory = String.format(OUTPUT_SUBDIRECTORY_FORMAT, "classes", File.separator, "lib");

        super.copyDependencies(/*mavenProject, mavenSession, buildPluginManager, outputSubdirectory*/);
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
            ProtectionDomain protectionDomain = PackageMojo.class.getProtectionDomain();
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
