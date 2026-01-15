package com.example.demo.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.grafana.LgtmStackContainer

@TestConfiguration(proxyBeanMethods = false)
class TestObservationConfig {
    @Bean
    @ServiceConnection
    fun lgtmStackContainer() = LgtmStackContainer("grafana/otel-lgtm:latest")
}