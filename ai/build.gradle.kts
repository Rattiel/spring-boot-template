plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
}

dependencies {
    annotationProcessor(platform(projects.dependencies))
    annotationProcessor(libs.spring.boot.configuration.processor)

    implementation(platform(projects.dependencies))
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(projects.domain)
    implementation(projects.observability)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.spring)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(testFixtures(projects.domain))
    testImplementation(testFixtures(projects.observability))
}