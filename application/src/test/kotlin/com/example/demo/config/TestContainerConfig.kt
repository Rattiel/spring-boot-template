package com.example.demo.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import

@Import(TestObservationConfig::class, TestDatabaseConfig::class)
@TestConfiguration(proxyBeanMethods = false)
class TestContainerConfig