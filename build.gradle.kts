plugins {
    alias(libs.plugins.java.test.fixtures)
    alias(libs.plugins.kotlinx.kover)
}

subprojects {
    apply(plugin = "idea")
    apply(plugin = "java-test-fixtures")
    apply(plugin = "org.jetbrains.kotlinx.kover")

    rootProject.dependencies {
        kover(this@subprojects)
    }

    kover {
        currentProject {
            sources {
                excludedSourceSets.addAll(sourceSets.testFixtures.name)
            }
        }
    }
}

kover {
    reports {
        filters {
            excludes {
                annotatedBy("*Generated*")
                classes("com.example.demo.ApplicationKt")
            }
        }
    }
}