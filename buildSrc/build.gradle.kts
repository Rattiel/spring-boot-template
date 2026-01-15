plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.org.asciidoctor.jvm.convert.gradle.plugin)
}

kotlin {
    jvmToolchain(21)
}