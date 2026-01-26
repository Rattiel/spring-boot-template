package com.example.demo.service

import com.example.demo.dto.PostParam
import com.example.demo.exception.BoardErrorCode
import com.example.demo.exception.ErrorCodeException
import com.example.demo.model.Category
import com.example.demo.model.Post
import com.example.demo.model.User
import com.example.demo.repository.CategoryJpaRepository
import com.example.demo.repository.PostJpaRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.core.PropertyReferenceException
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull

class SimplePostServiceTests {
    private val categoryJpaRepository = mockk<CategoryJpaRepository>()
    private val postJpaRepository = mockk<PostJpaRepository>()
    private val service = SimplePostService(categoryJpaRepository, postJpaRepository)

    @DisplayName("생성 테스트")
    @Nested
    inner class CreateTests {
        @DisplayName("입력이 유효하면 게시글이 생성된다")
        @Test
        fun `should create post when inputs are valid`() {
            // given
            val categoryId = 1L
            val param = PostParam(categoryId = categoryId)
            val user = User()
            val category = Category(id = categoryId)

            every { categoryJpaRepository.findByIdOrNull(categoryId) } returns category
            every { postJpaRepository.save(any()) } answers { firstArg() }

            // when
            val result = service.create(param, user)

            // then
            assertEquals(param.title, result.title)
            assertEquals(user, result.writer)
            assertEquals(category, result.category)

            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(categoryId) }
            verify(exactly = 1) { postJpaRepository.save(any()) }
        }

        @DisplayName("존재하지 않는 카테고리로 생성 요청 시 예외가 발생한다")
        @Test
        fun `should throw exception when creating with non-existent category`() {
            // given
            val categoryId = 999L
            val param = PostParam(categoryId = categoryId)
            val user = User()

            every { categoryJpaRepository.findByIdOrNull(categoryId) } returns null

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.create(param, user)
            }
            assertEquals(BoardErrorCode.INVALID_POST_CATEGORY, exception.errorCode)

            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(categoryId) }
        }

        @DisplayName("제목이 빈 문자열이면 생성 시 예외가 발생한다")
        @Test
        fun `should throw exception when creating with empty title`() {
            // given
            val categoryId = 1L
            val param = PostParam(categoryId = categoryId, title = "")
            val user = User()
            val category = Category(id = categoryId)

            every { categoryJpaRepository.findByIdOrNull(categoryId) } returns category

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.create(param, user)
            }
            assertEquals(BoardErrorCode.INVALID_POST_TITLE, exception.errorCode)

            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(categoryId) }
        }

        @DisplayName("본문이 빈 문자열이면 생성 시 예외가 발생한다")
        @Test
        fun `should throw exception when creating with empty content`() {
            // given
            val categoryId = 1L
            val param = PostParam(categoryId = categoryId, content = "")
            val user = User()
            val category = Category(id = categoryId)

            every { categoryJpaRepository.findByIdOrNull(categoryId) } returns category

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.create(param, user)
            }
            assertEquals(BoardErrorCode.INVALID_POST_CONTENT, exception.errorCode)

            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(categoryId) }
        }
    }

    @DisplayName("조회 테스트")
    @Nested
    inner class ReadTests {
        @DisplayName("해당 게시물을 조회하면 조회수가 증가한다")
        @Test
        fun `should increment view count when finding post`() {
            // given
            val postId = 1L
            val viewCount = 0L
            val post = Post(id = postId, viewCount = viewCount)

            every { postJpaRepository.findByIdOrNull(postId) } returns post
            every { postJpaRepository.save(any()) } answers { firstArg() }

            // when
            val result = service.findById(postId)

            // then
            assertEquals(postId, result.id)
            assertEquals(viewCount + 1, result.viewCount)

            verify(exactly = 1) { postJpaRepository.findByIdOrNull(postId) }
            verify(exactly = 1) { postJpaRepository.save(post) }
        }

        @DisplayName("해당 게시물이 존재하지 않으면 예외가 발생한다")
        @Test
        fun `should throw exception when finding non-existent post`() {
            // given
            val postId = 999L
            every { postJpaRepository.findByIdOrNull(postId) } returns null

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.findById(postId)
            }
            assertEquals(BoardErrorCode.NOT_FOUND_POST, exception.errorCode)

            verify(exactly = 1) { postJpaRepository.findByIdOrNull(postId) }
        }

        @DisplayName("카테고리로 페이징된 게시글 목록을 조회한다")
        @Test
        fun `should find posts by category id with paging`() {
            // given
            val categoryId = 1L
            val pageable = PageRequest.of(0, 10)
            val category = Category(id = categoryId)
            val posts = listOf(
                Post(title = "Title 1", category = category),
                Post(title = "Title 2", category = category),
            )

            every { postJpaRepository.findByCategoryId(categoryId, pageable) } returns PageImpl(
                posts, pageable, posts.size.toLong()
            )
            every { categoryJpaRepository.existsById(categoryId) } returns true

            // when
            val result = service.findByCategoryId(categoryId, pageable)

            // then
            assertEquals(2, result.content.size)
            assertEquals("Title 1", result.content[0].title)

            verify(exactly = 1) { postJpaRepository.findByCategoryId(categoryId, pageable) }
            verify(exactly = 1) { categoryJpaRepository.existsById(categoryId) }
        }

        @DisplayName("페이징할 카테고리가 존재하지 않으면 예외가 발생한다")
        @Test
        fun `should throw exception when paging by non-existent category`() {
            // given
            val categoryId = 999L
            val pageable = PageRequest.of(0, 10)

            every { categoryJpaRepository.existsById(categoryId) } returns false

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.findByCategoryId(categoryId, pageable)
            }
            assertEquals(BoardErrorCode.NOT_FOUND_CATEGORY, exception.errorCode)

            verify(exactly = 1) { categoryJpaRepository.existsById(categoryId) }
        }

        @DisplayName("페이징 옵션이 잘못되면 않으면 예외가 발생한다")
        @Test
        fun `should throw exception when paging options are invalid`() {
            // given
            val categoryId = 1L
            val pageable = PageRequest.of(0, 10)

            every { postJpaRepository.findByCategoryId(categoryId, pageable) } throws mockk<PropertyReferenceException>().also {
                every { it.propertyName } returns "error"
            }
            every { categoryJpaRepository.existsById(categoryId) } returns true

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.findByCategoryId(categoryId, pageable)
            }
            assertEquals(BoardErrorCode.INVALID_POST_SORT_PROPERTY, exception.errorCode)

            verify(exactly = 1) { postJpaRepository.findByCategoryId(categoryId, pageable) }
            verify(exactly = 1) { categoryJpaRepository.existsById(categoryId) }
        }
    }

    @DisplayName("수정 테스트")
    @Nested
    inner class UpdateTests {
        @DisplayName("입력이 유효하면 게시글이 수정된다")
        @Test
        fun `should update post when writer matches and inputs are valid`() {
            // given
            val postId = 1L
            val categoryId = 2L
            val user = User()
            val category = Category(id = categoryId)
            val param = PostParam(categoryId = categoryId, title = "Updated Title", content = "Updated Content")
            val existingPost = Post(id = postId, writer = user)

            every { postJpaRepository.findByIdOrNull(postId) } returns existingPost
            every { categoryJpaRepository.findByIdOrNull(categoryId) } returns category
            every { postJpaRepository.save(any()) } answers { firstArg() }

            // when
            val result = service.update(postId, param, user)

            // then
            assertEquals(param.title, result.title)
            assertEquals(param.content, result.content)
            assertEquals(category, result.category)

            verify(exactly = 1) { postJpaRepository.save(any()) }
            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(categoryId) }
            verify(exactly = 1) { postJpaRepository.save(any()) }
        }

        @DisplayName("존재하지 않는 게시글을 수정 요청 시 예외가 발생한다")
        @Test
        fun `should throw exception when updating non-existent post`() {
            // given
            val postId = 999L
            val param = PostParam()
            val user = User()

            every { postJpaRepository.findByIdOrNull(postId) } returns null

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.update(postId, param, user)
            }
            assertEquals(BoardErrorCode.NOT_FOUND_POST, exception.errorCode)

            verify(exactly = 1) { postJpaRepository.findByIdOrNull(postId) }
        }

        @DisplayName("존재하지 않는 카테고리로 수정 요청 시 예외가 발생한다")
        @Test
        fun `should throw exception when updating with non-existent category id`() {
            // given
            val postId = 1L
            val categoryId = 2L
            val user = User()
            val param = PostParam(categoryId = categoryId)
            val post = Post(id = postId)

            every { postJpaRepository.findByIdOrNull(postId) } returns post
            every { categoryJpaRepository.findByIdOrNull(categoryId) } returns null

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.update(postId, param, user)
            }
            assertEquals(BoardErrorCode.INVALID_POST_CATEGORY, exception.errorCode)

            verify(exactly = 1) { postJpaRepository.findByIdOrNull(postId) }
            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(categoryId) }
        }

        @DisplayName("작성자가 일치하지 않으면 수정 시 권한 예외가 발생한다")
        @Test
        fun `should throw exception when updating with different writer`() {
            // given
            val postId = 1L
            val owner = User(id = "owner")
            val otherUser = User(id = "stranger")
            val param = PostParam()
            val post = Post(id = postId, writer = owner)

            every { postJpaRepository.findByIdOrNull(postId) } returns post

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.update(postId, param, otherUser)
            }
            assertEquals(BoardErrorCode.NOT_POST_OWNER, exception.errorCode)

            verify(exactly = 1) { postJpaRepository.findByIdOrNull(postId) }
        }

        @DisplayName("제목이 빈 문자열이면 수정 시 예외가 발생한다")
        @Test
        fun `should throw exception when updating with empty title`() {
            // given
            val postId = 1L
            val categoryId = 1L
            val user = User()
            val category = Category(id = categoryId)
            val param = PostParam(categoryId = categoryId, title = "")
            val post = Post(id = postId, writer = user)

            every { postJpaRepository.findByIdOrNull(postId) } returns post
            every { categoryJpaRepository.findByIdOrNull(categoryId) } returns category

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.update(postId, param, user)
            }
            assertEquals(BoardErrorCode.INVALID_POST_TITLE, exception.errorCode)

            verify(exactly = 1) { postJpaRepository.findByIdOrNull(postId) }
            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(categoryId) }
        }

        @DisplayName("본문이 빈 문자열이면 수정 시 예외가 발생한다")
        @Test
        fun `should throw exception when updating with empty content`() {
            // given
            val postId = 1L
            val categoryId = 1L
            val user = User()
            val category = Category(id = categoryId)
            val param = PostParam(categoryId = categoryId, content = "")
            val post = Post(id = postId, writer = user)

            every { postJpaRepository.findByIdOrNull(postId) } returns post
            every { categoryJpaRepository.findByIdOrNull(categoryId) } returns category

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.update(postId, param, user)
            }
            assertEquals(BoardErrorCode.INVALID_POST_CONTENT, exception.errorCode)

            verify(exactly = 1) { postJpaRepository.findByIdOrNull(postId) }
            verify(exactly = 1) { categoryJpaRepository.findByIdOrNull(categoryId) }
        }
    }

    @DisplayName("삭제 테스트")
    @Nested
    inner class DeleteTests {
        @DisplayName("입력이 유효하면 게시글이 삭제된다")
        @Test
        fun `should delete post when inputs are valid`() {
            // given
            val postId = 1L
            val user = User()
            val post = Post(id = postId, writer = user)

            every { postJpaRepository.findByIdOrNull(postId) } returns post
            justRun { postJpaRepository.delete(any()) }

            // when
            service.delete(postId, user)

            // then
            verify(exactly = 1) { postJpaRepository.findByIdOrNull(postId) }
            verify(exactly = 1) { postJpaRepository.delete(post) }
        }

        @DisplayName("존재하지 않는 게시글 삭제 시 예외가 발생한다")
        @Test
        fun `should throw exception when deleting non-existent post`() {
            // given
            val postId = 999L
            val user = User()

            every { postJpaRepository.findByIdOrNull(postId) } returns null

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.delete(postId, user)
            }
            assertEquals(BoardErrorCode.NOT_FOUND_POST, exception.errorCode)

            verify(exactly = 1) { postJpaRepository.findByIdOrNull(postId) }
        }

        @DisplayName("작성자가 일치하지 않으면 삭제 시 권한 예외가 발생한다")
        @Test
        fun `should throw exception when deleting with different writer`() {
            // given
            val postId = 1L
            val writer = User(id = "owner")
            val otherUser = User(id = "stranger")
            val post = Post(id = postId, writer = writer)

            every { postJpaRepository.findByIdOrNull(postId) } returns post

            // when & then
            val exception = assertThrows<ErrorCodeException> {
                service.delete(postId, otherUser)
            }
            assertEquals(BoardErrorCode.NOT_POST_OWNER, exception.errorCode)

            verify(exactly = 1) { postJpaRepository.findByIdOrNull(postId) }
        }
    }
}