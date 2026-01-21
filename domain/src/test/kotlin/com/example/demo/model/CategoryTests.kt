package com.example.demo.model

import com.example.demo.config.JpaConfig
import com.example.demo.config.TestContainerConfig
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(TestContainerConfig::class, JpaConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryTests {
    @Autowired
    lateinit var entityManager: EntityManager
}