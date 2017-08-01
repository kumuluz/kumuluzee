package com.kumuluz.ee.maven.plugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

public abstract class AbstractCopyDependenciesAndWebappMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager buildPluginManager;

    public void copyDependencies(/*MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager buildPluginManager*/)
            throws MojoExecutionException {
        copyDependencies(/*mavenProject, mavenSession, buildPluginManager, */null);
    }

    public void copyDependencies(/*MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager buildPluginManager, */String outputSubdirectory)
            throws MojoExecutionException {
        String OUTPUT_DIRECTORY_FORMAT = "%s%s%s";

        String outputDirectory = mavenProject.getBuild().getDirectory();
        outputDirectory = outputSubdirectory == null
                ? String.format(OUTPUT_DIRECTORY_FORMAT, outputDirectory, File.separator, "dependency")
                : String.format(OUTPUT_DIRECTORY_FORMAT, outputDirectory, File.separator, outputSubdirectory);

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
    }

    public void addWebapp(MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager buildPluginManager) {
        // TODO
    }
}
