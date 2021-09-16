package com.github.skhatri.gradle.init.model


data class RepositorySpec(val id: String, val url: String?, val flatDir: String?)

data class PluginSpec(val id: String, val classpath: String?)
data class PluginConfig(
    val plugins: List<PluginSpec>?,
    val repositories: List<RepositorySpec>?
)
