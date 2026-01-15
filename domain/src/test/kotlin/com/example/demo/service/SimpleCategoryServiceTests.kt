package com.example.demo.service

import com.example.demo.dto.CategoryParam
import com.example.demo.exception.BoardErrorCode
import com.example.demo.exception.ErrorCodeException
import com.example.demo.model.Category
import com.example.demo.repository.CategoryJpaRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class SimpleCategoryServiceTests {
    private val categoryJpaRepository = mockk<CategoryJpaRepository>()
    private val service = SimpleCategoryService(categoryJpaRepository)

    @DisplayName("생성 테스트")
    @Nested
    inner class CreateTests {
        @DisplayName("이름이 유효하면 카테고리가 생성된다")
        @Test
        fun `should create category when inputs are valid`() {
            // given
            val param = CategoryParam(name = "Category 1")

            every { categoryJpaRepository.save(any()) } answers { firstArg() }

            // when
            val result = service.create(param)

            // then
            assertEquals(param.name, result.name)

            verify(exactly = 1) { categoryJpaRepository.save(any()) }
        }

        @DisplayName("이름이 빈 문자열이면 생성 시 예외가 발생한다")
        @Test
        fun `should throw exception when creating with empty name`() {
            // given
            val param = CategoryParam(name = "")

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.create(param)
            }
            assertEquals(BoardErrorCode.INVALID_CATEGORY_NAME, exception.errorCode)
        }
    }

    @DisplayName("조회 테스트")
    @Nested
    inner class ReadTests {
        @DisplayName("해당 카테고리를 조회한다")
        @Test
        fun `should find category by id`() {
            // given
            val id = 1L
            val category = Category(id = id, name = "Category 1")

            every { categoryJpaRepository.findByIdOrNull(id) } returns category

            // when
            val result = service.findById(id)

            // then
            assertEquals(category.id, result.id)
            assertEquals(category.name, result.name)

            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(id) }
        }

        @DisplayName("해당 카테고리가 존재하지 않으면 예외가 발생한다")
        @Test
        fun `should throw exception when finding non-existent category`() {
            // given
            val id = 999L
            every { categoryJpaRepository.findByIdOrNull(id) } returns null

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.findById(id)
            }
            assertEquals(BoardErrorCode.NOT_FOUND_CATEGORY, exception.errorCode)

            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(id) }
        }

        @DisplayName("전체 카테고리 목록을 조회한다")
        @Test
        fun `should find all categories`() {
            // given
            val categories = listOf(
                Category(name = "Cat 1"),
                Category(name = "Cat 2"),
            )
            every { categoryJpaRepository.findAll() } returns categories

            // when
            val result = service.findAll()

            // then
            assertIterableEquals(categories, result)

            verify(exactly = 1) { categoryJpaRepository.findAll() }
        }
    }

    @DisplayName("수정 테스트")
    @Nested
    inner class UpdateTests {
        @DisplayName("입력이 유효하면 카테고리 이름이 수정된다")
        @Test
        fun `should update category name when id exists and input is valid`() {
            // given
            val id = 1L
            val param = CategoryParam()
            val category = Category(id = id)

            every { categoryJpaRepository.findByIdOrNull(id) } returns category
            every { categoryJpaRepository.save(any()) } answers { firstArg() }

            // when
            val result = service.update(id, param)

            // then
            assertEquals(param.name, result.name)

            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(id) }
            verify(exactly = 1) { categoryJpaRepository.save(any()) }
        }

        @DisplayName("존재하지 않는 카테고리를 수정 요청 시 예외가 발생한다")
        @Test
        fun `should throw exception when updating non-existent category`() {
            // given
            val id = 999L
            val param = CategoryParam()

            every { categoryJpaRepository.findByIdOrNull(id) } returns null

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.update(id, param)
            }
            assertEquals(BoardErrorCode.NOT_FOUND_CATEGORY, exception.errorCode)

            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(id) }
        }

        @DisplayName("수정할 이름이 빈 문자열이면 예외가 발생한다")
        @Test
        fun `should throw exception when updating with empty name`() {
            // given
            val id = 1L
            val param = CategoryParam(name = "")
            val category = Category(id = id)

            every { categoryJpaRepository.findByIdOrNull(id) } returns category

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.update(id, param)
            }
            assertEquals(BoardErrorCode.INVALID_CATEGORY_NAME, exception.errorCode)

            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(id) }
        }
    }

    @DisplayName("삭제 테스트")
    @Nested
    inner class DeleteTests {
        @DisplayName("입력이 유효하면 카테고리가 삭제된다")
        @Test
        fun `should delete category when inputs are valid`() {
            // given
            val id = 1L
            val category = Category(id = id)

            every { categoryJpaRepository.findByIdOrNull(id) } returns category
            justRun { categoryJpaRepository.delete(any()) }

            // when
            service.delete(id)

            // then
            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(id) }
            verify(exactly = 1) { categoryJpaRepository.delete(category) }
        }

        @DisplayName("존재하지 않는 카테고리 삭제 시 예외가 발생한다")
        @Test
        fun `should throw exception when deleting non-existent category`() {
            // given
            val id = 999L
            every { categoryJpaRepository.findByIdOrNull(id) } returns null

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.delete(id)
            }
            assertEquals(BoardErrorCode.NOT_FOUND_CATEGORY, exception.errorCode)

            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(id) }
        }
    }
}