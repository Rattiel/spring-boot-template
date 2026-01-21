plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.org.asciidoctor.jvm.convert)
}

dependencies {
    asciidoctorExt(platform(projects.dependencies))
    asciidoctorExt(libs.spring.restdocs.asciidoctor)

    annotationProcessor(platform(projects.dependencies))
    annotationProcessor(libs.spring.boot.configuration.processor)

    api(libs.jackson.module.kotlin)
    api(libs.spring.boot.starter.webmvc)

    implementation(platform(projects.dependencies))
    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    implementation(projects.domain)
    implementation(projects.observability)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.spring)
    testImplementation(libs.spring.boot.restdocs)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.restdocs.mockmvc)
    testImplementation(testFixtures(projects.domain))
    testImplementation(testFixtures(projects.observability))

    testFixturesApi(libs.spring.boot.starter.security.test)
    testFixturesApi(libs.spring.boot.starter.webmvc.test)

    testFixturesImplementation(platform(projects.dependencies))
    testFixturesImplementation(libs.spring.boot.starter.oauth2.resource.server)
    testFixturesImplementation(libs.spring.security.test)
}

tasks.asciidoctor {
    dependsOn(tasks.test)
}