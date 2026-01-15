package com.example.demo.model

import com.example.demo.config.JpaConfig
import com.example.demo.config.TestContainerConfig
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(TestContainerConfig::class, JpaConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryTests {
    @Autowired
    lateinit var entityManager: EntityManager
}