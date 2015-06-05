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

- Servlet 3.1 (Jetty)
- JSP 2.3 (RI JSP)
- EL 3.0 (RI UEL)
- CDI 1.2 (RI Weld)
- JPA 2.1 (RI EclipseLink)
- JAX-RS 2.0 (RI Jersey)
- Bean Validation 1.1 (RI Hibernate validator)
- JSON-P 1.0 (RI JSONP)

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
    <version>1.0.0</version>
</dependency>
```

To choose your Java EE components include them as your dependencies. To view whats you can include
please refer to the documentation.

You can download the binaries from the [releases page](https://github.com/TFaga/KumuluzEE/releases).

## Getting started

KumuluzEE allows you to quickly and efficiently bootstrap a Java EE application using just the
components that you need so that your app remains light and fast. We will be using Maven to create
a sample app.

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
    <kumuluzee.version>1.0.0</kumuluzee.version>
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
provided it is supported. Now lets add Jetty.

```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-servlet-jetty</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>
```

This is the bare minimum required to run an app with plain servlets. Lets try it out! First we will
add an simple servlet. We don't need to include a `web.xml` file as like application servers KumuluzEE supports
annotation scanning. However when and if you need it you can simply add it and it will be
automatically detected and used.

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

KumuluzEE will use a `webapp` folder at the root of your `resource` folder
to look for files and configuration regarding it. This is the only difference to the standard
Java EE file structure as the `webapp` folder has to be inside the `resource` folder, not alongside
it.

Create a webapp folder at the root of your resources directory and add a sample `index.html` file to
it. 

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Hello KumuluzEE</title>
</head>
<body>
    <p>Microservices with Java EE</p>
</body>
</html>
```

If you want to use the default settings this is all you need to do. The `kumuluzee-core` package
provides the class `com.kumuluz.ee.EeApplication` with a `main` method that will bootstrap your app.

If you have your project opened in an IDE (IntelliJ, Eclipse, ..) you can now start the app by
running the above class. If however you are looking to run it from the terminal (as will be the
case on a server and various PaaS environments) then you run it directly from the class files in
the `target` directory.

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
$ java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
```

Go to `http://localhost:8080/servlet` in your browser and you should see `"Simple servlet"` written.
You can also visit `http://localhost:8080` to view the `index.html` that we've written.

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

For a full blown tutorial, please read our [blog post](https://blog.kumuluz.com/architecture/2015/06/04/microservices-with-java-ee-and-kumuluzee).

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