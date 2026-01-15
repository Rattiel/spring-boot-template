plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(platform(projects.dependencies))
    implementation(libs.spring.boot.starter)
    implementation(projects.domain)
    implementation(projects.observability)
    implementation(projects.web)

    runtimeOnly(libs.postgresql)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(testFixtures(projects.domain))
    testImplementation(testFixtures(projects.observability))
    testImplementation(testFixtures(projects.web))

    testRuntimeOnly(libs.postgresql)

    developmentOnly(platform(projects.dependencies))
    developmentOnly(libs.spring.boot.devtools)
    developmentOnly(libs.spring.boot.docker.compose)
}

application {
    mainClass = "com.example.demo.ApplicationKt"
}

tasks.bootJar {
    from(project(":web").tasks.named("asciidoctor")) {
        into("static/docs")
    }
}