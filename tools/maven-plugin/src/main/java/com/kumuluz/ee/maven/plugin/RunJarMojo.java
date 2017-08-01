package com.kumuluz.ee.maven.plugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.File;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(
        name = "run-jar",
        defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class RunJarMojo extends AbstractPackageMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager buildPluginManager;

    @Parameter(defaultValue = "${project.build.directory}")
    private String outputDirectory;

    @Parameter(defaultValue = "${project.build.finalName}")
    private String finalName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        repackage(/*mavenProject, mavenSession, buildPluginManager*/);

        executeMojo(
                plugin(
                        groupId("org.codehaus.mojo"),
                        artifactId("exec-maven-plugin"),
                        version("1.6.0")
                ),
                goal("exec"),
                configuration(
                        element("executable", "java"),
                        element(name("arguments"),
                                element(name("argument"), "-jar"),
                                element(name("argument"), String.format("%s%s%s.%s", outputDirectory, File.separator, finalName, "jar"))
                        )
                ),
                executionEnvironment(mavenProject, mavenSession, buildPluginManager)
        );
    }
}
