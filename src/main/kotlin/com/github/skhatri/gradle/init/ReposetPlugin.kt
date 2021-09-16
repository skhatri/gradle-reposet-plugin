package com.github.skhatri.gradle.init

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.gradle.api.*
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.invocation.Gradle
import java.io.File

open class ReposetPlugin : Plugin<Gradle> {

    companion object Factory {

        data class RepositorySpec(val id: String, val url: String?, val flatDir: String?)

        data class PluginSpec(val id: String, val classpath: String?)
        data class PluginConfig(
            val plugins: List<PluginSpec>?,
            val lang: List<String>?,
            val repositories: List<RepositorySpec>?
        )

        class YamlSupport {
            private val yamlMapper = YAMLMapper()
            private val factory = YAMLFactory()

            init {
                yamlMapper.registerModule(KotlinModule())
            }

            fun toMap(fileName: String): Map<*, *> {
                return yamlMapper.readValue(File(fileName), Map::class.java)
            }

            fun toList(fileName: String): List<ObjectNode> {
                val parser = factory.createParser(File(fileName))
                val typeRef = object : TypeReference<ObjectNode>() {}
                return yamlMapper.readValues(parser, typeRef).readAll()
            }

            fun <T> toInstance(fileName: String, clz: Class<T>): T {
                return yamlMapper.readValue(File(fileName), clz)
            }

            fun <T> toInstance(data: ByteArray, clz: Class<T>): T {
                return yamlMapper.readValue(data, clz)
            }
        }
    }

    @Override
    override fun apply(gradle: Gradle) {
        val pluginConfig = loadConfig(gradle)

        gradle.addProjectEvaluationListener(object : ProjectEvaluationListener {
            override fun beforeEvaluate(p: Project) {
                configureRepositories(pluginConfig, p)
                addDependencies(pluginConfig, p)
                pluginConfig.plugins?.filter { it.classpath == null }?.forEach { spec ->
                    p.pluginManager.apply(spec.id)
                }
            }

            override fun afterEvaluate(p: Project, p1: ProjectState) {
                pluginConfig.plugins?.forEach { spec ->
                    if (!p.pluginManager.hasPlugin(spec.id)) {
                        p.pluginManager.apply(spec.id)
                    }
                }
            }
        })
    }

    private fun loadConfig(g: Gradle): PluginConfig {
        val pluginName = "reposet"
        val envFile = System.getenv("REPOSET_FILE")
        var configFile = ""
        if (envFile != null) {
            val fName = File(envFile)
            if (fName.exists() && fName.isFile) {
                configFile = envFile
            }
        }
        if (configFile == "") {
            configFile = listOf<String>(
                "./$pluginName.yaml",
                "${g.gradleUserHomeDir}/init.d/$pluginName.yaml",
                "${g.gradleHomeDir}/init.d/$pluginName.yaml",
            ).find {
                val f = File(it)
                f.exists() && f.isFile
            }
                ?: throw GradleException("REPOSET_FILE is not set and/or $pluginName.yaml is expected in one of current_dir or gradle init.d directories")
        }
        return YamlSupport().toInstance(configFile, PluginConfig::class.java)
    }

    private fun addDependencies(
        pluginConfig: PluginConfig,
        project: Project
    ) {
        pluginConfig.plugins?.forEach { spec ->
            spec.classpath?.let { cp ->
                val dep = cp.split(":")
                project.buildscript.dependencies.add(
                    "classpath",
                    DefaultExternalModuleDependency(dep[0], dep[1], dep[2])
                )
            }
        }
        listOf(
            "com.fasterxml.jackson.core:jackson-core:2.12.4",
            "com.fasterxml.jackson.core:jackson-databind:2.12.4",
            "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.4",
            "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.4",
            "com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4"
        ).forEach { dep ->
            val parts = dep.split(":")
            project.buildscript.dependencies.add(
                "classpath", DefaultExternalModuleDependency(parts[0], parts[1], parts[2])
            )
        }
    }

    private fun configureRepositories(
        pluginConfig: PluginConfig,
        project: Project
    ) {
        pluginConfig.repositories?.forEach { repo ->
            val resolvedRepo = if (repo.flatDir?.isNotEmpty() == true) {
                project.repositories.flatDir(mapOf("dirs" to listOf(repo.flatDir)))
            } else if (repo.url?.isNotEmpty() == true) {
                project.repositories.maven(object : Action<MavenArtifactRepository> {
                    override fun execute(mr: MavenArtifactRepository) {
                        mr.name = repo.id
                        mr.url = project.uri(repo.url)
                    }
                })
            } else {
                when (repo.id) {
                    "mavenCentral" -> project.repositories.mavenCentral()
                    "mavenLocal" -> project.repositories.mavenLocal()
                    else -> project.repositories.mavenCentral()
                }
            }
            project.repositories.add(resolvedRepo)
            project.buildscript.repositories.add(resolvedRepo)
        }

        if (0 == (pluginConfig.repositories?.size ?: 0)) {
            project.repositories.add(project.repositories.mavenCentral())
            project.buildscript.repositories.add(project.repositories.mavenCentral())
        }
    }
}
