package com.example.demo

import com.example.demo.config.TestContainerConfig
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestContainerConfig::class)
@SpringBootTest
class ApplicationTests {
    @DisplayName("컨텍스트 로드 테스트")
    @Test
    fun contextLoads() {
    }
}