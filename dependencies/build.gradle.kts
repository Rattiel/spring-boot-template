plugins {
    alias(libs.plugins.java.platform)
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform(libs.spring.ai.bom))
    api(platform(libs.spring.boot.dependencies))
}