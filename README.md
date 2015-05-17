# KumuluzEE
[![Build Status](https://img.shields.io/travis/TFaga/KumuluzEE/master.svg?style=flat)](https://travis-ci.org/TFaga/KumuluzEE)

> Lightweight framework for creating small standalone Java EE applications in a micro service way.

KumuluzEE allows you to simply with minimal or no configuration create a standalone lightweight Java EE application
that can be started as any other JAR app.

The framework automates the tasks, related to the deployment and configuration of Java EE applications and makes it seamless,
thus overcoming the major drawback of the microservice architecture and eliminating the need for an application server. 

KumuluzEE has been designed to use the standard Java EE technologies and APIs. 
Therefore, it is particularly suitable for existing enterprise Java EE developers,
who would like to leverage their skills, but progressively move from monolithic to microservice
design patterns.

Primary features:

- No need for a traditional application server. Run your app anywhere Java runs as well as in PaaS
and Docker-like environments
- Complete control over what Java EE components are included. Only include what you need and make
your app lightweight.
- Minimal to no configuration settings up the base server. Develop like any other Java EE application
- Quick startup time, low resource consumption and stateless scalability accelerates development
- Simplified unit and integration testing

The goal is to support as many Java EE components as possible. Currently the following components are
supported with more being added over time:

- Servlet 3.1, JSP 2.3 (Jetty)
- CDI 1.1 (RI Weld)
- JPA 2.1 (RI EclipseLink)
- Bean Validation 1.1 (RI Hibernate validator)
- JAX-RS 2.0 (RI Jersey)

If you already know how to use Java EE, then you already know how to use KumuluzEE. Its dead simple,
see the [getting started](#getting-started) section to create your first light-weight standalone
Java EE app in 5 minutes.

## Usage

Add the core module of the library as a dependency to your project.
 
Maven:

```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-core</artifactId>
    <version>1.0.0-alpha.1</version>
</dependency>
```

Gradle:

```groovy
runtime 'com.kumuluz.ee:kumuluzee-core:1.0.0-alpha.1'
```

To choose your Java EE components include them as your dependencies. To view whats you can include
please refer to the documentation.

You can download the binaries from the [releases page](https://github.com/TFaga/KumuluzEE/releases).

## Getting started

KumuluzEE allows you to quickly and efficiently bootstrap a Java EE application using just the
components that you need so that your app remains light and fast. We will be using Maven to create
a sample app as that is our system of choice, however if you prefer Gradle or some other Java
build/dependency system you can use that as well. Please refer to the full documentation to learn
more. 

### Create the project

Let's create a new project.

```bash
$ mvn -B archetype:generate \
    -DarchetypeGroupId=org.apache.maven.archetypes \
    -DgroupId=com.acme.app \
    -DartifactId=app
```

Once created you must first add the appropriate dependencies. As mentioned the framework is completely
modular which means that apart from the core functionality every component is packaged as a
separate module and must be included explicitly as a dependency in order to use it. The framework
will automatically detect which modules are included in the class path and bootstrap them together.

All modules are versioned and released together which helps us reduce cross version conflicts and
bugs. So it is recommended to define a property with the current version of KumuluzEE and use it
with every dependency.

> NOTE: Use the same version for every module as not doing so might result in unexpected behavior.

```xml
<properties>
    <kumuluzee.version>1.0.0-alpha.1</kumuluzee.version>
</properties>
```

First include the `core` module which includes the bootstrapping logic and configurations.

```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-core</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>
```

Now the core itself won't do much without something to run. At the very least we have to include an
HTTP server that will process our apps requests and of course process our servlets as they are the
backbone of Java EE. We prefer to use Jetty as the servlet implementation for its high performance
and small footprint. If however you prefer a different server (like Tomcat) you can easily do so
provided it is supported. Please refer to the full documentation to view supported servers. Now
lets add Jetty.

```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-servlet-jetty</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>
```

This is the bare minimum required to run an app with plain servlets. Lets try it out! First we will
add an simple servlet. We don't need to include a `web.xml` file as like application servers
KumuluzEE supports annotation scanning. However when and if you need it you can simply add it and
it will be automatically detected and used.

```java
package com.acme.app;

@WebServlet("/servlet")
public class SimpleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("Simple servlet");
    }
}
```

Now all that remains is a class with a `main` method and a single line of code to start the app.
Remember the goal is to run the app as a standalone Java application which requires an entry point.

```java
package com.acme.app;

import com.kumuluz.ee.EeApplication;

public class Run {

    public static void main(String args[]) throws Exception {

        EeApplication app = new EeApplication();
    }
}
```

If you have your project opened in an IDE (IntelliJ, Eclipse, ..) you can now start the app by
running the above class. If however you are looking to run it from the terminal (as will be the
case on a server) then you have one of two options; either run it directly from the class files in
the `target` directory or build a fat ja.

### Run from the target directory

To run from the target directory you must include the `maven-dependency-plugin` to your `pom.xml`
file which will copy all your dependencies together with your classes.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>2.10</version>
    <executions>
        <execution>
            <id>copy-dependencies</id>
            <phase>package</phase>
            <goals>
                <goal>copy-dependencies</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
Run `maven install` and then you can start your app using the following command:

```bash
$ java -cp target/classes:target/dependency/* com.acme.app.Run
```

Go to `http://localhost:8080/servlet` in your browser and you should see `"Simple servlet"` written.


### Build a fat jar

To build a fat jar you must include the `maven-shade-plugin` to your `pom.xml` file.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>2.3</version>
    <executions>
        <!-- Run shade goal on package phase -->
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <!-- Add your main class here -->
                        <mainClass>com.acme.app.Run</mainClass>
                    </transformer>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Run `maven install` and your jar will be available in the `target` directory. Then run it.

```bash
$ java -jar target/app-1.0.0-SNAPSHOT.jar
```

Go to `http://localhost:8080/servlet` in your browser and you should see `"Simple servlet"` written.

### Add the JAX-RS component to create REST services

To enable JAX-RS in your application add the apropriate dependency to your application and the
framework will automatically detect and enable it.

```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jax-rs</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>
```

And thats it. Lets now add an application.

```java
package com.acme.app

@ApplicationPath("/")
public class RestApplication extends Application {

}
```

And a rest resource.

```java
package com.acme.app;

@Path("/rest")
public class RestResource {

    @GET
    public Response getResources() {
        
        Map<String, String> json = new HashMap<>();
        json.put("framework", "KumuluzEE");
        
        return Response.ok(json).build();
    }
}
```

Run the app and visit `http://localhost:8080/rest` and you should see the following.

```json
{
    "framework": "KumuluzEE"
}
```

Congratulations you have created a simple and lightweight Java EE app that follows the micro service
pattern and can be easily deployed in a variety of cloud environments.

For an in depth guide and documentation visit the project wiki.

## Building

Ensure you have JDK 8 (or newer), Maven 3.2.1 (or newer) and Git installed

```bash
java -version
mvn -version
git --version
```

First clone the KumuluzEE repository:

```bash
git clone https://github.com/TFaga/KumuluzEE.git
cd KumuluzEE
```
    
To build KumuluzEE run:

```bash
mvn install
```

This will build all modules and run the testsuite. 
    
Once completed you will find the build archives in the modules respected `target` folder.

## Changelog

Recent changes can be viewed on Github on the [Releases Page](https://github.com/TFaga/KumuluzEE/releases)

## Contribute

See the [contributing docs](https://github.com/TFaga/KumuluzEE/blob/master/CONTRIBUTING.md)

When submitting an issue, please follow the [guidelines](https://github.com/TFaga/KumuluzEE/blob/master/CONTRIBUTING.md#bugs).

When submitting a bugfix, write a test that exposes the bug and fails before applying your fix. Submit the test alongside the fix.

When submitting a new feature, add tests that cover the feature.

## License

GPLv3