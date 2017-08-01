package com.kumuluz.ee.maven.plugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.File;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * @author Benjamin Kastelic
 */
@Mojo(
        name = "run-exploded",
        defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class RunExplodedMojo extends AbstractCopyDependenciesAndWebappMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager buildPluginManager;

    @Override
    public void execute() throws MojoExecutionException {
        copyDependencies(/*mavenProject, mavenSession, buildPluginManager*/);

        String CLASSPATH_FORMAT = "target%1$sclasses%2$starget%1$sdependency%1$s*";

        executeMojo(
                plugin(
                        groupId("org.codehaus.mojo"),
                        artifactId("exec-maven-plugin"),
                        version("1.6.0")
                ),
                goal("java"),
                configuration(
                        element(name("mainClass"), "com.kumuluz.ee.EeApplication"),
                        element(name("arguments"),
                                element(name("argument"), "-classpath"),
                                element(name("argument"), String.format(CLASSPATH_FORMAT, File.separator, File.pathSeparator)))
                ),
                executionEnvironment(mavenProject, mavenSession, buildPluginManager)
        );
    }
}
