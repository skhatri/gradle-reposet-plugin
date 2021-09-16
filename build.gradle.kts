ext {
    group = "com.github.skhatri.gradle.plugins"
    version = "0.1"
}
plugins {
    id("idea")
    kotlin("jvm") version "1.5.21"

    id("java-gradle-plugin")                          
    id("maven-publish")                               
    id("com.gradle.plugin-publish") version "0.14.0"

}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    testImplementation(gradleTestKit())

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.fasterxml.jackson.core:jackson-core:2.12.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.4")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.4")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:2.8")
}


gradlePlugin {
    plugins { 
        create("com.github.skhatri.reposet") { 
            id = "com.github.skhatri.reposet"
            displayName = "A plugin to configure gradle repositories and plugins for one or all builds"
            description = "This plugin will provide config to set repositories and plugins for your projects. This can be useful to add default repository for personal or enterprise projects"
            implementationClass = "com.github.skhatri.gradle.init.ReposetPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/skhatri/gradle-reposet-plugin"
    vcsUrl = "https://github.com/skhatri/gradle-reposet-plugin.git"
    tags = listOf("dynamic", "init", "repository", "reposet", "buildscript", "initscript") 
}

publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("../local-plugin-repository")
        }
    }
}

