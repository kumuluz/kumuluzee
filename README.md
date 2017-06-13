# KumuluzEE
[![Build Status](https://img.shields.io/travis/kumuluz/kumuluzee/master.svg?style=flat)](https://travis-ci.org/kumuluz/kumuluzee)

> Lightweight open-source framework for developing microservices using standard Java EE technologies and migrating Java EE to cloud-native architecture.

KumuluzEE is a lightweight framework for developing microservices using standard Java EE technologies and migrating 
existing Java EE applications to microservices. KumuluzEE packages microservices as standalone JARs.
KumuluzEE microservices are lightweight and optimized for size and start-up time.
They fit perfectly with Docker containers.

KumuluzEE also provides extensions for developing common patterns in cloud-native architectures,
including configuration, logging, discovery, circuit-breakers, metrics, security, event streaming and more.

KumuluzEE has been designed to use the standard Java EE technologies and APIs with optional extensions for easier
development of cloud-native microservices. Therefore, it is particularly suitable for existing enterprise Java EE developers,
who would like to leverage their skills, but progressively move from monolithic to microservice design patterns.

Primary features:

- No need for a traditional application server. Run your app anywhere Java runs as well as in PaaS
and Docker-like environments
- Allows you to develop microservices using standard Java EE technologies.
- Allows you to gradually migrate existing Java EE applications to microservices and cloud-native architecture.
- Complete control over what Java EE components and its implementations are included. Only include what you need and make
your app lightweight.
- Minimal to no configuration settings up the base server. Develop like any other Java EE application
- Quick startup time, low resource consumption and stateless scalability accelerates development
- Extend your microservices with common cloud-native patterns, such as config, discovery, logging, circuit-breakers,
etc. KumuluzEE provides all the building blocks.

The goal is to support as many Java EE components as possible. Currently the following components are
supported with more being added over time:

- Servlet 3.1 (Jetty)
- CDI 1.2 (RI Weld)
- JAX-RS 2.1. (RI Jersey)
- JSON-P 1.0 (RI JSONP)
- JSP 2.3 (Jetty Apache Jasper)
- JPA 2.1 (RI EclipseLink)
- JPA 2.1 (RI Hibernate)
- EL 3.0 (RI UEL)
- JAX-WS (RI Metro)
- JSF 2.2 (RI Mojarra)
- WebSocket 1.1 (Jetty)
- Bean Validation 1.1 (RI Hibernate validator)
- JTA 1.2 (Narayana)


## KumuluzEE extensions

In addition to the standard Java EE components, KumuluzEE also comes with several extensions that complement and extend
its functionality. The extensions will follow the same modular concept as the Java EE components in which you can chose
if you want to use it as well as the underlying implementation of the extension. Most extensions are geared towards creating
cloud-native microservices including configuration, logging, discovery, circuit-breakers, metrics, security, event streaming and more.
The following extensions are available with more planned soon:

- [KumuluzEE Config (File and environment variables built-in)](https://github.com/kumuluz/kumuluzee/wiki/Configuration)
- [KumuluzEE Config (Config server etcd/Consul)](https://github.com/kumuluz/kumuluzee-config)
- [KumuluzEE REST (or implementation of common, advanced and flexible REST API functionalities and patterns. Includes support for exposing JPA entities through REST)](https://github.com/kumuluz/kumuluzee/ kumuluzee-rest)
- [KumuluzEE Logs (For advanced microservice framework for logging)](https://github.com/kumuluz/kumuluzee-logs)
- [KumuluzEE Discovery (For dynamic service discovery (etcd or Consul). Fully compatible with Kubernetes)](https://github.com/kumuluz/kumuluzee-discovery)
- [KumuluzEE Metrics (For easy collection and reporting of performance metrics)](https://github.com/kumuluz)
- [KumuluzEE Security (For easy integration with OAuth2/OpenID identity and access management providers)](https://github.com/kumuluz/kumuluzee-security)
- [KumuluzEE Circuit Breaker (For implementing circuit breakers and decoupling microservices)](https://github.com/kumuluz)
- [KumuluzEE Kafka (For event streaming support using Apache Kafka)](https://github.com/kumuluz/kumuluzee-kafka)


If you already know how to use Java EE, then you already know how to use KumuluzEE. It’s dead simple,
see the [getting started](https://github.com/kumuluz/KumuluzEE/wiki/Getting-started) wiki section to create your first light-weight standalone
Java EE app in 5 minutes.

## Usage

KumuluzEE ships with a BOM (bill of materials) which you can use to easily add the various components you need without
worrying about the versions and their compatibility as it is required that all components are the same version. 

Using maven add the BOM module of the library as a dependency to your project. The `${kumuluzee.version}`
variables represents the most current minor version. You can see the available version on the
[Release Page](https://github.com/kumuluz/KumuluzEE/releases).

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-bom</artifactId>
            <version>${kumuluz.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Now you can either choose any single combinations of Java EE components or use one of the common pre-built profiles that
ship as part of KumuluzEE. You can chose any of the following profiles:

```xml
<!-- MicroProfile 1.0 -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-microProfile-1.0</artifactId>
</dependency>
```

To choose your Java EE components with fine grain control include them as your dependencies. You can chose any of the
following artifacts:

```xml
<!-- Servlet (Jetty) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-servlet-jetty</artifactId>
</dependency>

<!-- WebSocket (Jetty) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-websocket-jetty</artifactId>
</dependency>

<!-- JSP (Jetty Apache Jasper) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jsp-jetty</artifactId>
</dependency>

<!-- EL (UEL) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-el-uel</artifactId>
</dependency>

<!-- CDI (Weld) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-cdi-weld</artifactId>
</dependency>

<!-- JPA (EclipseLink) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jpa-eclipselink</artifactId>
</dependency>

<!-- or -->

<!-- JPA (Hibernate) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jpa-hibernate</artifactId>
</dependency>

<!-- JAX-RS (Jersey) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jax-rs-jersey</artifactId>
</dependency>

<!-- JSF (Mojarra) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jsf-mojarra</artifactId>
</dependency>

<!-- Bean Validation (Hibernate validator) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-bean-validation-hibernate-validator</artifactId>
</dependency>

<!-- JSON-P -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-json-p-jsonp</artifactId>
</dependency>
```

To choose your KumuluzEE extensions include them as your dependencies. You can find the available implementations and
options at their respected project pages:

- [KumuluzEE Logs](https://github.com/kumuluz/kumuluzee-logs)

## Getting started

You can find the getting started guide on the projects [wiki](https://github.com/kumuluz/KumuluzEE/wiki/Getting-started).
You can find samples [for Java EE and KumuluzEE]( https://github.com/kumuluz/kumuluzee-samples)

## Building

Ensure you have JDK 8 (or newer), Maven 3.2.1 (or newer) and Git installed

```bash
java -version
mvn -version
git --version
```

First clone the KumuluzEE repository:

```bash
git clone https://github.com/kumuluz/KumuluzEE.git
cd KumuluzEE
```
    
To build KumuluzEE run:

```bash
mvn clean package
```

This will build all modules and run the testsuite. 
    
Once completed you will find the build archives in the modules respected `target` folder.

## Changelog

Recent changes can be viewed on Github on the [Releases Page](https://github.com/kumuluz/KumuluzEE/releases)

## Contribute

See the [contributing docs](https://github.com/kumuluz/KumuluzEE/blob/master/CONTRIBUTING.md)

When submitting an issue, please follow the [guidelines](https://github.com/kumuluz/KumuluzEE/blob/master/CONTRIBUTING.md#bugs).

When submitting a bugfix, write a test that exposes the bug and fails before applying your fix. Submit the test alongside the fix.

When submitting a new feature, add tests that cover the feature.

## License

MIT
