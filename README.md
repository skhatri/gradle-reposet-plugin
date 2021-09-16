### Intro
This is a gradle plugin to configure repositories and plugins dynamically for one or many of your projects.


### Building

```
gradle clean build
```

### Testing
```
cd example
gradle clean build -I init.gradle.kts
```

### Customizing
Additional repositories and plugins can be added for your project by providing a reposet.yaml file

```
plugins:
  - id: "org.springframework.boot"
    classpath: "org.springframework.boot:spring-boot-gradle-plugin:2.5.4"
  - id: "io.spring.dependency-management"
    classpath: "io.spring.gradle:dependency-management-plugin:1.0.9.RELEASE"
  - id: "org.sonarqube"
    classpath: "org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:2.8"
  - id: "jacoco"
  - id: "java"
  - id: "org.jetbrains.kotlin.plugin.spring"
    classpath: "org.jetbrains.kotlin:kotlin-allopen:1.5.21"
  - id: "org.jetbrains.kotlin.jvm"
    classpath: "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21"
  - id: "scala"

repositories:
  - id: "mavenCentral"
  - id: "libs"
    flatDir: "./../build/libs"
  - id: "my-enterprise-repo1"
    url: "http://some-company.internal/maven2/"
```

See build.gradle.kts and init.gradle.kts



