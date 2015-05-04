# KumuluzEE
[![Build Status](https://img.shields.io/travis/TFaga/KumuluzEE/master.svg?style=flat)](https://travis-ci.org/TFaga/KumuluzEE)

> Lightweight framework for creating small standalone Java EE applications in a micro service way.

The Standard approach for deploying Java EE applications is packing all components into single EAR/WAR archive and deploying the archive on a application server.
Although this approach has several advantages, particularly from the ease-of-development perspective,
it leads to monolithic architecture, makes applications difficult to maintain, and – particularly important – makes such applications more difficult to scale,
especially in PaaS (cloud) and Docker-like environments.

Microservice architecture addresses these shortcomings by decomposing an application into a set of microservices.
Each microservice has well-defined functionalities and an interface for communication with other services (such as REST, WSDL, or even RMI).
However the former approach makes it increasingly difficult to use and follow the microservice pattern
as either you are highly dependent on an application server and its configuration (HA, clustering, ...)
or have to include along a separate application server with each instance that you then have to separately manage.

KumuluzEE automates the tasks, related to the deployment and configuration and makes it seamless,
thus overcoming the major drawback of microservice architecture and eliminating the need for an application server. 

KumuluzEE has been designed to use the standard Java EE technologies and APIs. 
Therefore, it is particularly suitable for existing enterprise Java EE developers,
who would like to leverage their skills, but progressively move from monolithic to microservice architecture.

The framework collects together the various Java EE components into a simple standalone package that
can be run as any other JAR. Apart from the core bootstrap functionality, the framework is completely
modular and as such allows the developer, with the help of Maven, Gradle or plain old jar files,
to handpick the components that he needs for his application. Whether it be only JAX-RS and JPA or
a bigger stack with EJB, JAX-WS, JSF, JMS and many more. After the desired components are selected
they are automatically included and you can start building your app as you would any other Java EE
app with an application server.

The goal is to support as many Java EE components as possible. Currently the following components are
supported with more being added over time:

- Servlet 3.1 (Grizzly)
- CDI 1.1 (RI Weld)
- JPA 2.1 (RI EclipseLink)
- Bean Validation 1.1 (RI Hibernate validator)
- JAX-RS 2.0 (RI Jersey)

If you already know how to use Java EE, then you already know how to use KumuluzEE. Its dead simple,
see the [getting started](#getting-started) section to create your first light-weight standalone
Java EE app in 5 minutes.

## Usage

You can download the binaries from the [releases page](https://github.com/TFaga/KumuluzEE/releases).

## Getting started

TODO

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

MIT