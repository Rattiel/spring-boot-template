@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "demo"

module(name = ":ai", path = "ai")
module(name = ":application", path = "application")
module(name = ":dependencies", path = "dependencies")
module(name = ":domain", path = "domain")
module(name = ":observability", path = "observability")
module(name = ":web", path = "web")

fun module(name: String, path: String) {
    include(name)
    project(name).projectDir = file(path)
}