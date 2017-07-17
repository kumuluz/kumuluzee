# KumuluzEE Maven Plugin

> KumuluzEE Maven Plugin for the Kumuluz EE microservice framework

TODO - description

## Usage

Include the plugin in your project:

```xml
<plugin>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-maven-plugin</artifactId>
    <version>2.3.0-SNAPSHOT</version>
    <executions>
        <execution>
            <id>package</id>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <finalName/>
                <outputDirectory/>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Goals

* __loader:package__

    Builds an "uber" JAR from the current project

### Parameters

* __finalName__

    Final name of the generated "uber" JAR.
    
    __Default value is__: `${project.build.finalName}` or `${project.artifactId}-${project.version}`
    
* __outputDirectory__

    Directory containing the generated JAR.
    
    __Default value is__: `${project.build.directory}`


## Run
Start the application using the following command:
```cmd
java -jar ${project.build.finalName}.jar
```
Example:
```cmd
java -jar my-app-1.0.0-SNAPSHOT.jar
```