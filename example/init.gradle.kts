import java.io.File
import com.github.skhatri.gradle.init.ReposetPlugin

initscript {
    val reposetLib: List<String> = listOf(
        "com.fasterxml.jackson.core:jackson-core:2.12.4",
        "com.fasterxml.jackson.core:jackson-databind:2.12.4",
        "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.4",
        "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.4",
        "com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4",
        "com.github.skhatri.gradle.plugins:com.github.skhatri.reposet:0.1"
    )

    repositories {
        mavenCentral()
        flatDir {
            dirs = setOf(File("./../build/libs"))
        }
    }
    dependencies {
        reposetLib.forEach { dep ->
            classpath(dep)
        }
    }
}

apply<com.github.skhatri.gradle.init.ReposetPlugin>()

