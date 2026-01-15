package com.example.demo.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import

@Import(TestDatabaseConfig::class)
@TestConfiguration(proxyBeanMethods = false)
class TestContainerConfig