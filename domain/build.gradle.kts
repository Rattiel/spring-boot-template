plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.spring)
}

dependencies {
    kapt(platform(projects.dependencies))
    kapt(variantOf(libs.querydsl.apt) {
        classifier("jakarta")
    })

    annotationProcessor(platform(projects.dependencies))
    annotationProcessor(libs.spring.boot.configuration.processor)

    api(libs.spring.data.commons)
    api(libs.spring.tx)

    implementation(platform(projects.dependencies))
    implementation(libs.datasource.micrometer.spring.boot)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(variantOf(libs.querydsl.jpa) {
        classifier("jakarta")
    })
    implementation(projects.observability)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.spring.boot.starter.data.jpa.test)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(testFixtures(projects.observability))

    testRuntimeOnly(libs.postgresql)

    testFixturesImplementation(platform(projects.dependencies))
    testFixturesImplementation(libs.spring.boot.test)
    testFixturesImplementation(libs.spring.boot.testcontainers)
    testFixturesImplementation(libs.testcontainers.postgresql)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

kapt {
    arguments {
        arg("querydsl.generatedAnnotationClass", "com.querydsl.core.annotations.Generated")
    }
}

tasks.configureEach {
    if (name == "kaptTestKotlin" || name == "kaptTestFixturesKotlin") {
        enabled = false
    }
}