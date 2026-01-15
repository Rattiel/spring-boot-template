package com.example.demo.repository

import com.example.demo.config.JpaConfig
import com.example.demo.config.TestContainerConfig
import org.hibernate.SessionFactory
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(
    properties = ["spring.jpa.properties.hibernate.generate_statistics=true"]
)
@Import(TestContainerConfig::class, JpaConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostJpaRepositoryTests {
    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var sessionFactory: SessionFactory

    @Autowired
    lateinit var repository: PostJpaRepository

    @BeforeEach
    fun setup() {
        entityManager.flush()
        entityManager.clear()
        sessionFactory.statistics.clear()
    }

    @DisplayName("findByCategoryId 테스트")
    @Nested
    inner class FindByCategoryIdTests {
        @DisplayName("2개의 쿼리만 발생해야 한다")
        @Test
        fun `should execute exactly 2 queries`() {
            // given
            val categoryId = 3L
            val pageable = PageRequest.of(0, 10)

            // when
            val result = repository.findByCategoryId(categoryId, pageable)

            // then
            assertEquals(10, result.content.size)
            result.content.forEach {
                assertTrue(sessionFactory.persistenceUnitUtil.isLoaded(it, "category"))
            }
            assertEquals(2, sessionFactory.statistics.prepareStatementCount)
        }

        @DisplayName("카테고리 ID와 페이징 정보가 주어지면 올바른 개수의 게시글이 반환된다")
        @ParameterizedTest(name = "카테고리(ID:{0}) - {1} 번 페이지를 {2} 개씩 조회 시 {3} 개 반환 (총 {4} 개)")
        @CsvSource(
            "3, 0, 10, 50, 10",
            "5, 1, 10, 15, 5",
            "1, 0, 10, 0,  0",
        )
        fun `should return correct post counts based on category and pagination`(
            categoryId: Long,
            page: Int,
            size: Int,
            expectedTotal: Long,
            expectedContentSize: Int,
        ) {
            // given
            val pageable = PageRequest.of(page, size)

            // when
            val result = repository.findByCategoryId(categoryId, pageable)

            // then
            assertEquals(expectedTotal, result.totalElements)
            assertEquals(expectedContentSize, result.content.size)
        }

        @DisplayName("정렬 조건이 주어지면 조건에 맞게 정렬된 게시글이 반환된다")
        @ParameterizedTest(name = "카테고리(ID:{0}) - {1} 필드 {2} 정렬 시 첫 번째 값은 ''{3}''")
        @CsvSource(
            "6, title,     ASC,  Test Post [Cat 6] - Title 100",
            "6, id,        DESC, 100",
            "6, writer.id, DESC, test-user-99",
        )
        fun `should return sorted posts based on sort properties`(
            categoryId: Long,
            sortField: String,
            directionStr: String,
            expectedFirstValue: String,
        ) {
            // given
            val direction = Sort.Direction.valueOf(directionStr.uppercase())
            val pageable = PageRequest.of(0, 10, Sort.by(direction, sortField))

            // when
            val result = repository.findByCategoryId(categoryId, pageable)
            val firstItem = result.content.first()

            // then
            val actualValue = when (sortField) {
                "title" -> firstItem.title
                "id" -> firstItem.id.toString()
                "writer.id" -> firstItem.writer.id
                else -> throw IllegalArgumentException("Test setup error: unknown field")
            }
            assertEquals(expectedFirstValue, actualValue)
        }
    }
}