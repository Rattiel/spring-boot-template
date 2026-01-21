package com.example.demo.model

import com.example.demo.config.JpaConfig
import com.example.demo.config.TestContainerConfig
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(TestContainerConfig::class, JpaConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostTests {
    @Autowired
    lateinit var factory: EntityManagerFactory

    @Autowired
    lateinit var entityManager: EntityManager

    @DisplayName("category가 즉시 로딩 되어야 한다")
    @Test
    fun `should eagerly fetch category`() {
        // given
        val category = Category(name = "테스트 카테고리").also {
            entityManager.persist(it)
        }

        val post = Post(
            category = category,
            title = "테스트 제목",
            content = "테스트 내용"
        ).also {
            entityManager.persist(it)
        }

        entityManager.flush()
        entityManager.clear()

        // when
        val foundPost = entityManager.find(Post::class.java, post.id)

        // then
        assertNotNull(foundPost)
        assertTrue(factory.persistenceUnitUtil.isLoaded(foundPost, "category"))
    }

    @DisplayName("Post-Category 관계 및 Cascade 테스트")
    @Nested
    inner class CascadeTests {
        @DisplayName("Category 삭제 시 @OnDelete(CASCADE)에 의해 연관된 Post도 함께 삭제되어야 한다")
        @Test
        fun `should delete post when category is deleted`() {
            // given
            val category = Category(name = "테스트 카테고리").also {
                entityManager.persist(it)
            }

            val post = Post(
                category = category,
                title = "테스트 제목",
                content = "테스트 내용"
            ).also {
                entityManager.persist(it)
            }

            entityManager.flush()
            entityManager.clear()

            // when
            val foundCategory = entityManager.find(Category::class.java, category.id)
            entityManager.remove(foundCategory)

            entityManager.flush()
            entityManager.clear()

            // then
            val deletedPost = entityManager.find(Post::class.java, post.id)
            assertNull(deletedPost, "카테고리가 삭제되면 게시글도 삭제되어야 합니다.")
        }

        @Test
        @DisplayName("Post 저장 시 Category에 CascadeType.PERSIST가 없으므로, 비영속 Category 사용 시 예외가 발생해야 한다")
        fun `should throw exception when saving post with transient category`() {
            // given
            val newCategory = Category(name = "새 카테고리")

            val post = Post(
                category = newCategory,
                title = "저장 실패 테스트",
                content = "내용"
            )

            // when & then
            assertThrows<IllegalStateException> {
                entityManager.persist(post)
                entityManager.flush()
            }
        }
    }
}