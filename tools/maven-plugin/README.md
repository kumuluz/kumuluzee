# KumuluzEE Maven Plugin

> KumuluzEE Maven Plugin for the Kumuluz EE microservice framework

TODO - description

## Usage

Include the plugin in your project:

```xml
<plugin>
    <groupId>com.kumuluz.ee</groupId>
    <artifactId>kumuluzee-maven-plugin</artifactId>
    <version>2.4.0-SNAPSHOT</version>
</plugin>
```

### Goals

* __kumuluzee:copy-dependencies__
    
    Copy dependencies and prepare for execution in an exploded class and dependency runtime.


* __kumuluzee:repackage__

    Repackages existing JAR archives so that they can be executed from the command line using `java -jar`.
    
    ###### Parameters
    
    * __finalName__
    
        Final name of the generated "uber" JAR.
        
        __Default value is__: `${project.build.finalName}` or `${project.artifactId}-${project.version}`
        
    * __outputDirectory__
    
        Directory containing the generated JAR.
        
        __Default value is__: `${project.build.directory}`
    
* __kumuluzee:run__

    Run the application in an exploded class and dependency runtime.
    