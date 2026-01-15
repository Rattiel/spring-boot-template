package com.example.demo.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer

@TestConfiguration(proxyBeanMethods = false)
class TestDatabaseConfig {
    @Bean
    @ServiceConnection
    fun postgres() = PostgreSQLContainer("postgres:latest").also {
        it.withInitScripts("schema.sql", "dummy_data.sql")
    }
}