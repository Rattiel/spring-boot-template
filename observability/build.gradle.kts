plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
}

dependencies {
    api(libs.micrometer.core)
    api(libs.micrometer.tracing)
    api(libs.slf4j.api)

    implementation(platform(projects.dependencies))
    implementation(libs.opentelemetry.logback.appender)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.aspectj)
    implementation(libs.spring.boot.starter.opentelemetry)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.boot.starter.actuator.test)
    testImplementation(libs.spring.boot.starter.aspectj.test)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.opentelemetry.test)

    testFixturesImplementation(platform(projects.dependencies))
    testFixturesImplementation(libs.spring.boot.test)
    testFixturesImplementation(libs.spring.boot.testcontainers)
    testFixturesImplementation(libs.testcontainers.grafana)
}