# KumuluzEE
[![Build Status](https://img.shields.io/travis/kumuluz/kumuluzee/master.svg?style=flat)](https://travis-ci.org/kumuluz/kumuluzee)

> Lightweight open-source framework for developing microservices using standard Java/JavaEE/JakartaEE/EE4J technologies, extending them with Node.js, Go and other languages, and migrating to cloud-native architecture.

KumuluzEE is a lightweight framework for developing microservices using standard Java/JavaEE/JakartaEE/EE4J technologies, extending them with Node.js, Go and other languages, and migrating existing applications to microservices and cloud-native architecture. KumuluzEE packages microservices as standalone JARs. KumuluzEE microservices are lightweight and optimized for size and start-up time.
They fit perfectly with Docker containers. KumuluzEE microservices are fully compatible with Kubernetes. 

KumuluzEE also provides extensions for developing common patterns in cloud-native architectures, including configuration, logging, discovery, fault tolerance with circuit-breakers, metrics, security, event streaming and more.

KumuluzEE has been designed to use the standard Java/JavaEE/JakartaEE/EE4J technologies and APIs with optional extensions for easier development of cloud-native microservices. Therefore, it is particularly suitable for existing enterprise Java developers, who would like to leverage their skills, but progressively move from monolithic to microservice design patterns.

KumuluzEE provides full support for **Java**, including Java SE 9/10/11 and higher versions and Java EE 8. In addition to Java, KumuluzEE supports several programming languages, including **Node.js** and **Go**. Support for additional languages will be added soon. For these languages, KumuluzEE also provides support for service configuration and discovery. 

KumuluzEE is Eclipse MicroProfile compliant and provides support for MicroProfile 1.0, 1.1 and 1.2. We will support higher versions soon. It implements MicroProfile Config 1.3, MicroProfile Health 1.0, MicroProfile Fault Tolerance 1.0, MicroProfile Metrics 1.1, MicroProfile JWT Authentication 1.0 and MicroProfile Open Tracing 1.0 APIs.

## Getting started

If you already know how to use Java/JavaEE/JakartaEE/EE4J, then you already know how to use KumuluzEE. It is simple and straightforward. 

See the [getting started](https://github.com/kumuluz/KumuluzEE/wiki/Getting-started) wiki section to create your first light-weight standalone Java microservice in 5 minutes. 

Read [tutorials](https://ee.kumuluz.com/tutorial/) at our home page.

Refer to [samples](https://github.com/kumuluz/kumuluzee-samples) for more examples.

## Features

Primary features:

- No need for a traditional application server. Run your app anywhere Java runs as well as in PaaS
and Docker-like environments
- Allows you to develop microservices using standard Java technologies and extend them with Node.js, Go and other languages.
- Allows you to gradually migrate existing Java applications to microservices and cloud-native architecture.
- Complete control over what Java/JavaEE/JakartaEE/EE4J components and its implementations are included. Only include what you need and make your app lightweight.
- Minimal to no configuration settings up the base server. Develop like any other Java application.
- Quick startup time, low resource consumption and stateless scalability accelerates development.
- Extend your microservices with common cloud-native patterns, such as config, discovery, logging, fault tolerance, circuit-breakers, etc. KumuluzEE provides all the building blocks.

KumuluzEE provides support for various Java/JavaEE/JakartaEE/EE4J APIs and components. The goal is to support as many Java components as possible (contributions welcome). 

## Java/JavaEE/JakartaEE/EE4J components

Currently the following components are supported with more being added over time:

- Servlet 3.1 (Jetty)
- CDI 2.0 (RI Weld)
- JAX-RS 2.1. (RI Jersey)
- JSON-P 1.1 (RI JSONP)
- JSON-B 1.0 (RI Yasson)
- JSP 2.3 (Jetty Apache Jasper)
- JPA 2.2 (RI EclipseLink)
- JPA 2.2 (RI Hibernate)
- EL 3.0 (RI UEL)
- JAX-WS 2.3 (RI Metro)
- JAX-WS 2.3 (Apache CXF)
- JSF 2.3 (RI Mojarra)
- WebSocket 1.1 (Jetty)
- Bean Validation 2.0 (RI Hibernate validator)
- JTA 1.3 (Narayana)
- Java Mail 1.6 (RI JavaMail)

## Additional features

KumuluzEE provides additional features, which are described on the [project Wiki]( https://github.com/kumuluz/kumuluzee/wiki), particularly:
- [Configuration framework](https://github.com/kumuluz/kumuluzee/wiki/Configuration) for easy and efficient configuration of microservices from various sources, such as environment variables, configuration files (yaml), properties, etc.
- [TLS/SSL support](https://github.com/kumuluz/kumuluzee/wiki/TLS-SSL-support) for configuring TLS/SSL.

KumuluzEE also provides support for [**Uber JARs**](https://github.com/kumuluz/kumuluzee/wiki/Uber-JAR-support). With the KumuluzEE Maven plugin, you can pack and run each microservice as a single, self-contained Uber-JAR. Details are described later in this document.

## KumuluzEE projects

In addition to the standard Java EE components, KumuluzEE also comes with several projects that complement and extend its functionality. The projects follow the same modular concept as the Java components in which you can chose if you want to use it as well as the underlying implementation of the project. Most projects are geared towards creating cloud-native microservices including configuration, logging, discovery, fault tolerance including circuit-breakers, metrics, security, event streaming and more. Projects also extend KumuluzEE with technologies, such as Ethereum for blockchain microservices, gRPC, GraphQL, and others. 
The following projects are available with more planned soon:

- [KumuluzEE Config](https://github.com/kumuluz/kumuluzee/wiki/Configuration) (File and environment variables)
- [KumuluzEE Config with config server](https://github.com/kumuluz/kumuluzee-config) (Config server etcd/Consul)
- [KumuluzEE REST](https://github.com/kumuluz/kumuluzee-rest) (For implementation of common, advanced and flexible REST API functionalities and patterns. Includes support for exposing JPA entities through REST)
- [KumuluzEE Logs](https://github.com/kumuluz/kumuluzee-logs) (For advanced microservice framework for logging)
- [KumuluzEE Discovery](https://github.com/kumuluz/kumuluzee-discovery) (For dynamic service discovery (etcd or Consul). Fully compatible with Kubernetes)
- [KumuluzEE Metrics](https://github.com/kumuluz/kumuluzee-metrics) (For easy collection and reporting of performance metrics)
- [KumuluzEE Security](https://github.com/kumuluz/kumuluzee-security) (For easy integration with OAuth2/OpenID identity and access management providers)
- [KumuluzEE Health](https://github.com/kumuluz/kumuluzee-health) (For implementing health checks and exposing microservice health information)
- [KumuluzEE Fault Tolerance](https://github.com/kumuluz/kumuluzee-fault-tolerance) (For implementing fault tolerance patterns with microservices including circuit breakers and decoupling microservices)
- [KumuluzEE Event Streaming](https://github.com/kumuluz/kumuluzee-streaming) (For event streaming support using Apache Kafka)
- [KumuluzEE Reactive](https://github.com/kumuluz/kumuluzee-reactive) (For developing reactive microservices and integration with reactive streams (Vert.x and similar))
- [KumuluzEE CORS](https://github.com/kumuluz/kumuluzee-cors) (For Cross-Origin Resource Sharing (CORS) support)
- [KumuluzEE Swagger](https://github.com/kumuluz/kumuluzee-swagger) (For Swagger (OpenAPI 2) support and visualization)
- [KumuluzEE OpenAPI](https://github.com/kumuluz/kumuluzee-openapi) (For OpenAPI 3 support, interface generation and visualization)
- [KumuluzEE Testing](https://github.com/kumuluz/kumuluzee-testing) (Tools and utilities for testing KumuluzEE microservices)
- [KumuluzEE gRPC](https://github.com/kumuluz/kumuluzee-grpc) (Native support for gRPC based services)
- [KumuluzEE GraphQL](https://github.com/kumuluz/kumuluzee-graphql) (Native support for GraphQL)
- [KumuluzEE Ethereum](https://github.com/kumuluz/kumuluzee-ethereum) (For Ethereum-enabled blockchain microservices)
- **_new_** [KumuluzEE AMQP](https://github.com/kumuluz/kumuluzee-amqp) (Support for Advanced Message Queueing Protocol)


## MicroProfile

KumuluzEE is Eclipse MicroProfile compliant and provides support for:
- MicroProfile 1.0 
- MicroProfile 1.1
- MicroProfile 1.2
- MicroProfile 1.3
- MicroProfile 1.4
- MicroProfile 2.0
- MicroProfile 2.1

It implements the following MicroProfile APIs:
- [MicroProfile Config 1.3](https://github.com/kumuluz/kumuluzee-config-mp)
- [MicroProfile Health Check 1.0](https://github.com/kumuluz/kumuluzee-health)
- [MicroProfile Fault Tolerance 1.1](https://github.com/kumuluz/kumuluzee-fault-tolerance)
- [MicroProfile Metrics 1.1](https://github.com/kumuluz/kumuluzee-metrics)
- [MicroProfile JWT Authentication 1.1](https://github.com/kumuluz/kumuluzee-jwt-auth)
- [MicroProfile Rest Client 1.1](https://github.com/kumuluz/kumuluzee-rest-client)
- [MicroProfile OpenAPI 1.0.1](https://github.com/kumuluz/kumuluzee-openapi-mp)
- [MicroProfile Open Tracing 1.2.1](https://github.com/kumuluz/kumuluzee-opentracing)

## Usage

KumuluzEE ships with a BOM (bill of materials) which you can use to easily add the various components you need without worrying about the versions and their compatibility as it is required that all components are the same version. 

Using maven add the BOM module of the library as a dependency to your project. The `${kumuluzee.version}` variables represents the most current minor version. You can see the available version on the
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

Now you can choose either any single combinations of Java components or use one of the common pre-built profiles that ship as part of KumuluzEE. You can chose any of the following profiles:

MicroProfile 1.0
```xml
<!-- MicroProfile 1.0 -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-microProfile-1.0</artifactId>
</dependency>
```

MicroProfile 1.1
```xml
<!-- MicroProfile 1.1 -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-microProfile-1.1</artifactId>
</dependency>
```

MicroProfile 1.2
```xml
<!-- MicroProfile 1.2 -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-microProfile-1.2</artifactId>
</dependency>
```

MicroProfile 1.3
```xml
<!-- MicroProfile 1.3 -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-microProfile-1.3</artifactId>
</dependency>
```

MicroProfile 1.4
```xml
<!-- MicroProfile 1.4 -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-microProfile-1.4</artifactId>
</dependency>
```

MicroProfile 2.0
```xml
<!-- MicroProfile 2.0 -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-microProfile-2.0</artifactId>
</dependency>
```

MicroProfile 2.1
```xml
<!-- MicroProfile 2.1 -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-microProfile-2.1</artifactId>
</dependency>
```

To choose your Java components with fine grain control include them as your dependencies. You can chose any of the following artifacts:

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

<!-- JAX-WS (RI Metro) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jax-ws-metro</artifactId>
</dependency>

<!-- or -->

<!-- JAX-WS (Apache CXF) -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jax-ws-cxf</artifactId>
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

<!-- JSON-B -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-json-b-yasson</artifactId>
</dependency>

<!-- JTA -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-jta-narayana</artifactId>
</dependency>

<!-- JavaMail -->
<dependency>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-javamail-ri</artifactId>
</dependency>
```

To choose your KumuluzEE project, you simply include it as your dependency. You can find the available implementations and options at their respected project pages.

## Pack and run microservice as Uber JAR

KumuluzEE (version 2.4 and higher) provides support for packing and running microservices as Uber JARs. It also includes a Maven plugin that correctly packages the microservice. 

To package a Kumuluz EE microservice into an Uber JAR, you need to add the following plugin declaration into your 
REST module pom.xml:

```xml
<plugin>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-maven-plugin</artifactId>
    <version>${kumuluz.version}</version>
    <executions>
        <execution>
            <id>package</id>
            <goals>
                <goal>repackage</goal>
            </goals>
            <configuration>
                <finalName/>
                <outputDirectory/>
                <mainClass/>
                <webappDir/>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### Parameters

* __finalName__

    Final name of the generated "uber" JAR.
    
    __Default value is__: `${project.build.finalName}` or `${project.artifactId}-${project.version}`
    
* __outputDirectory__

    Directory containing the generated JAR.
    
    __Default value is__: `${project.build.directory}`


### Run
Start the application using the following command:
```cmd
java -jar ${project.build.finalName}.jar
```
Example:
```cmd
java -jar my-app-1.0.0-SNAPSHOT.jar
```

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
