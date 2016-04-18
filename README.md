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
- Choose your Java EE component implementations. If there is a problem with a single implementation,
you can simply add a different one.
- Minimal to no configuration settings up the base server. Develop like any other Java EE application
- Quick startup time, low resource consumption and stateless scalability accelerates development
- Simplified unit and integration testing

The goal is to support as many Java EE components as possible. Currently the following components are
supported with more being added over time:

- Servlet 3.1 (Jetty)
- WebSocket 1.1 (Jetty)
- JSP 2.3 (Jetty Apache Jasper)
- EL 3.0 (RI UEL)
- CDI 1.2 (RI Weld)
- JPA 2.1 (RI EclipseLink)
- JAX-RS 2.0 (RI Jersey)
- JSF 2.2 (RI Mojarra)
- Bean Validation 1.1 (RI Hibernate validator)
- JSON-P 1.0 (RI JSONP)

If you already know how to use Java EE, then you already know how to use KumuluzEE. Its dead simple,
see the [getting started](https://github.com/TFaga/KumuluzEE/wiki/Getting-started) wiki section to create your first light-weight standalone
Java EE app in 5 minutes.

## Usage

Using maven add the core module of the library as a dependency to your project. The `${kumuluzee.version}`
variables represents the most current minor version. You can see the available version on the
[Release Page](https://github.com/TFaga/KumuluzEE/releases).

```xml
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-core</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>
```

To choose your Java EE components include them as your dependencies. You can chose any of the
following artifacts:

```xml
<!-- Servlet (Jetty) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-servlet-jetty</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>

<!-- WebSocket (Jetty) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-websocket-jetty</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>

<!-- JSP (Jetty Apache Jasper) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jsp-jetty</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>

<!-- EL (UEL) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-el-uel</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>

<!-- CDI (Weld) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-cdi-weld</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>

<!-- JPA (EclipseLink) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jpa-eclipselink</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>

<!-- JAX-RS (Jersey) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jax-rs-jersey</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>

<!-- JSF (Mojarra) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jsf-mojarra</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>

<!-- Bean Validation (Hibernate validator) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-bean-validation-hibernate-validator</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>

<!-- JSON-P -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-json-p-jsonp</artifactId>
    <version>${kumuluzee.version}</version>
</dependency>
```

## Planned features

For full Java EE support please refer to our website for the enterprise version.

Additional implementations will also be added to existing components. Currently the following are planned:

- RESTEasy (JAX-RS)
- Hibernate (JPA)
- MyFaces (JSF)

The features will arrive separately following a series of betas, first of which will be available at the end of March/begining of April.
If the testing goes well, the final release should come soon after a few betas.

## Getting started

You can find the getting started guide on the projects [wiki](https://github.com/TFaga/KumuluzEE/wiki/Getting-started).

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
mvn clean package
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

MIT
