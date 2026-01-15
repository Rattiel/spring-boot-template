package com.example.demo.web.config

import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.jwt.JwtDecoder

@Import(SecurityConfig::class)
@TestConfiguration(proxyBeanMethods = false)
class TestSecurityConfig {
    @Bean
    fun jwtDecoder(): JwtDecoder {
        return mockk()
    }
}