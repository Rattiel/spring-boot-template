package com.example.demo.web.controller

import com.example.demo.model.Category
import com.example.demo.model.Post
import com.example.demo.service.PostService
import com.example.demo.web.config.TestSecurityConfig
import com.example.demo.web.config.WebConfig
import com.example.demo.web.request.PostRequest
import com.example.demo.web.test.support.WithJwt
import com.example.demo.web.test.support.uriDecode
import com.example.demo.web.test.support.withBearerToken
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.hamcrest.Matchers.hasItems
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.*
import tools.jackson.databind.ObjectMapper
import java.util.*

@AutoConfigureRestDocs
@Import(WebConfig::class, TestSecurityConfig::class, GlobalExceptionController::class)
@WebMvcTest(PostController::class)
class PostControllerTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var postService: PostService

    @DisplayName("생성 API")
    @Nested
    inner class CreateTests {
        @DisplayName("입력값이 유효하지 않으면 400 Bad Request를 반환한다")
        @Test
        @WithJwt(subject = "test-user")
        fun `should return 400 Bad Request when input is invalid`() {
            // given
            val request = PostRequest(
                title = "", content = "", categoryId = 1L
            )

            // when & then
            mockMvc.post("/post") {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header(HttpHeaders.ACCEPT_LANGUAGE, Locale.KOREAN.language)
            }.andExpect {
                status { isBadRequest() }

                jsonPath("$.type") { value("http://localhost:8080/docs/index.html#validation-error") }
                jsonPath("$.title") { value("잘못된 요청") }
                jsonPath("$.detail") { value("인자 유효성 검사에 실패했습니다.") }
                jsonPath("$.status") { value(HttpStatus.BAD_REQUEST.value()) }
                jsonPath("$.errors") {
                    value(hasItems(
                        mapOf(
                            "field" to "title",
                            "message" to "게시물 제목은 공백일 수 없습니다."
                        ),
                        mapOf(
                            "field" to "content",
                            "message" to "게시물 본문은 공백일 수 없습니다."
                        )
                    ))
                }
            }
        }

        @DisplayName("입력값이 유효하면 게시글을 생성하고 200 OK를 반환한다")
        @Test
        @WithJwt(subject = "test-user")
        fun `should create post and return 200 OK when input is valid`() {
            // given
            val request = PostRequest(
                title = "테스트 제목", content = "테스트 내용", categoryId = 1L
            )
            val post = Post()

            every { postService.create(any(), any()) } returns post

            // when & then
            mockMvc.post("/post") {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
            }.andDo {
                handle(
                    document(
                        "post-create",
                        preprocessRequest(prettyPrint(), withBearerToken()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 인증 토큰")
                        ),
                        requestFields(
                            fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                            fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 본문"),
                            fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID")
                        ),
                        responseFields(
                            fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                            fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                            fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 본문"),
                            fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("조회수"),

                            fieldWithPath("writer").type(JsonFieldType.OBJECT).description("작성자 정보"),
                            fieldWithPath("writer.id").type(JsonFieldType.STRING).description("작성자 ID"),

                            fieldWithPath("category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                            fieldWithPath("category.id").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                            fieldWithPath("category.name").type(JsonFieldType.STRING).description("카테고리 이름"),

                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                            fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 일시")
                        )
                    )
                )
            }

            verify(exactly = 1) {
                postService.create(
                    param = match { it.title == request.title && it.categoryId == request.categoryId },
                    user = match { it.id == "test-user" },
                )
            }
        }

        @DisplayName("인증 안된 요청이면 401 Unauthorized를 반환한다")
        @Test
        fun `should return 401 Unauthorized when request is unauthenticated`() {
            // given
            val request = PostRequest()

            // when & then
            mockMvc.post("/post") {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)

                header(HttpHeaders.ACCEPT_LANGUAGE, Locale.KOREAN.language)
            }.andExpect {
                status { isUnauthorized() }

                jsonPath("$.type") { value("http://localhost:8080/docs/index.html#unauthorized") }
                jsonPath("$.title") { value("인증 필요") }
                jsonPath("$.detail") { value("해당 리소스에 접근하기 위한 자격 증명이 없거나 유효하지 않습니다.") }
                jsonPath("$.status") { value(HttpStatus.UNAUTHORIZED.value()) }
                jsonPath("$.instance") { value("/post") }
            }
        }
    }

    @DisplayName("조회 API")
    @Nested
    inner class ReadTests {
        @DisplayName("ID에 해당하는 게시글이 존재하면 조회하고 200 OK를 반환한다")
        @Test
        fun `should return post and 200 OK when post exists`() {
            // given
            val id = 1L
            val post = Post(id = id)

            every { postService.findById(any()) } returns post

            // when & then
            mockMvc.get("/post/{id}", id) {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
            }.andDo {
                handle(
                    document(
                        "post-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("id").description("조회할 게시글 ID")
                        ),
                        responseFields(
                            fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                            fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                            fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 본문"),
                            fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("조회수"),

                            fieldWithPath("writer").type(JsonFieldType.OBJECT).description("작성자 정보"),
                            fieldWithPath("writer.id").type(JsonFieldType.STRING).description("작성자 ID"),

                            fieldWithPath("category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                            fieldWithPath("category.id").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                            fieldWithPath("category.name").type(JsonFieldType.STRING).description("카테고리 이름"),

                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                            fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 일시")
                        )
                    )
                )
            }

            verify(exactly = 1) { postService.findById(id = id) }
        }

        @DisplayName("카테고리별 게시글 목록을 조회하고 200 OK를 반환한다")
        @Test
        fun `should return posts by category and 200 OK`() {
            // given
            val categoryId = 1L
            val pageNumber = 0
            val pageSize = 10
            val sortField = "id"
            val sortDirection = Sort.Direction.DESC
            val category = Category(id = categoryId)
            val posts = listOf(
                Post(id = 1L, category = category),
                Post(id = 2L, category = category),
            )

            every { postService.findByCategoryId(any(), any()) } returns PageImpl(posts)

            // when & then
            mockMvc.get("/post") {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON

                param("categoryId", categoryId.toString())
                param("page", pageNumber.toString())
                param("size", pageSize.toString())
                param("sort", "$sortField,${sortDirection.name.lowercase()}")
            }.andExpect {
                status { isOk() }
            }.andDo {
                handle(
                    document(
                        "post-get-all-by-category",
                        preprocessRequest(prettyPrint(), uriDecode()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                            parameterWithName("categoryId").description("조회할 카테고리 ID"),
                            parameterWithName("page").description("페이지 번호").optional(),
                            parameterWithName("size").description("페이지 크기").optional(),
                            parameterWithName("sort").description("정렬 조건").optional()
                        ),
                        responseFields(
                            fieldWithPath("items").type(JsonFieldType.ARRAY).description("게시글 목록"),
                            fieldWithPath("items[].id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                            fieldWithPath("items[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                            fieldWithPath("items[].writer").type(JsonFieldType.OBJECT).description("작성자 정보"),
                            fieldWithPath("items[].writer.id").type(JsonFieldType.STRING).description("작성자 ID"),
                            fieldWithPath("items[].createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                            fieldWithPath("total").type(JsonFieldType.NUMBER).description("전체 게시글 수"),
                        )
                    )
                )
            }

            verify(exactly = 1) {
                postService.findByCategoryId(
                    categoryId = categoryId,
                    pageable = match {
                        it.pageNumber == pageNumber && it.pageSize == pageSize && it.sort.getOrderFor(sortField)?.direction == sortDirection
                    },
                )
            }
        }
    }

    @DisplayName("수정 API")
    @Nested
    inner class UpdateTests {
        @DisplayName("입력값이 유효하면 게시글을 수정하고 200 OK를 반환한다")
        @Test
        @WithJwt(subject = "test-user")
        fun `should update post and return 200 OK when input is valid`() {
            // given
            val id = 1L
            val request = PostRequest(
                title = "수정된 제목", content = "수정된 본문", categoryId = 1L
            )
            val post = Post(id = id)

            every { postService.update(any(), any(), any()) } returns post

            // when & then
            mockMvc.put("/post/{id}", id) {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
            }.andDo {
                handle(
                    document(
                        "post-update",
                        preprocessRequest(prettyPrint(), withBearerToken()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("id").description("수정할 게시글 ID")
                        ),
                        requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 인증 토큰")
                        ),
                        requestFields(
                            fieldWithPath("title").type(JsonFieldType.STRING).description("수정할 제목"),
                            fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 본문"),
                            fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("수정할 카테고리 ID")
                        ),
                        responseFields(
                            fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                            fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                            fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 본문"),
                            fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("조회수"),

                            fieldWithPath("writer").type(JsonFieldType.OBJECT).description("작성자 정보"),
                            fieldWithPath("writer.id").type(JsonFieldType.STRING).description("작성자 ID"),

                            fieldWithPath("category").type(JsonFieldType.OBJECT).description("카테고리 정보"),
                            fieldWithPath("category.id").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                            fieldWithPath("category.name").type(JsonFieldType.STRING).description("카테고리 이름"),

                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                            fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 일시")
                        )
                    )
                )
            }

            verify(exactly = 1) {
                postService.update(
                    id = id,
                    param = match { it.title == request.title && it.content == request.content },
                    user = match { it.id == "test-user" },
                )
            }
        }

        @DisplayName("입력값이 유효하지 않으면 400 Bad Request를 반환한다")
        @Test
        @WithJwt(subject = "test-user")
        fun `should return 400 Bad Request when input is invalid`() {
            // given
            val id = 1L
            val request = PostRequest(
                title = "", content = "", categoryId = 1L
            )

            // when & then
            mockMvc.put("/post/{id}", id) {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)

                header(HttpHeaders.ACCEPT_LANGUAGE, Locale.KOREAN.language)
            }.andExpect {
                status { isBadRequest() }

                jsonPath("$.type") { value("http://localhost:8080/docs/index.html#validation-error") }
                jsonPath("$.title") { value("잘못된 요청") }
                jsonPath("$.errors") {
                    value(hasItems(
                        mapOf(
                            "field" to "title",
                            "message" to "게시물 제목은 공백일 수 없습니다."
                        ),
                        mapOf(
                            "field" to "content",
                            "message" to "게시물 본문은 공백일 수 없습니다."
                        )
                    ))
                }
            }
        }

        @DisplayName("인증 안된 요청이면 401 Unauthorized를 반환한다")
        @Test
        fun `should return 401 Unauthorized when request is unauthenticated`() {
            // given
            val id = 1L
            val request = PostRequest()

            // when & then
            mockMvc.put("/post/{id}", id) {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)

                header(HttpHeaders.ACCEPT_LANGUAGE, Locale.KOREAN.language)
            }.andExpect {
                status { isUnauthorized() }

                jsonPath("$.type") { value("http://localhost:8080/docs/index.html#unauthorized") }
                jsonPath("$.title") { value("인증 필요") }
                jsonPath("$.detail") { value("해당 리소스에 접근하기 위한 자격 증명이 없거나 유효하지 않습니다.") }
                jsonPath("$.status") { value(HttpStatus.UNAUTHORIZED.value()) }
                jsonPath("$.instance") { value("/post/$id") }
            }
        }
    }

    @DisplayName("삭제 API")
    @Nested
    inner class DeleteTests {
        @DisplayName("게시글 삭제에 성공하면 204 No Content를 반환한다")
        @Test
        @WithJwt(subject = "test-user")
        fun `should delete post and return 204 No Content`() {
            // given
            val id = 1L
            justRun { postService.delete(any(), any()) }

            // when & then
            mockMvc.delete("/post/{id}", id).andExpect {
                status { isNoContent() }
            }.andDo {
                handle(
                    document(
                        "post-delete",
                        preprocessRequest(prettyPrint(), withBearerToken()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("id").description("삭제할 게시글 ID")
                        ),
                        requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 인증 토큰")
                        )
                    )
                )
            }

            verify(exactly = 1) {
                postService.delete(
                    id = id,
                    user = match { it.id == "test-user" },
                )
            }
        }

        @DisplayName("인증 안된 요청이면 401 Unauthorized를 반환한다")
        @Test
        fun `should return 401 Unauthorized when request is unauthenticated`() {
            // given
            val id = 1L

            // when & then
            mockMvc.delete("/post/{id}", id) {
                header(HttpHeaders.ACCEPT_LANGUAGE, Locale.KOREAN.language)
            }.andExpect {
                status { isUnauthorized() }

                jsonPath("$.type") { value("http://localhost:8080/docs/index.html#unauthorized") }
                jsonPath("$.title") { value("인증 필요") }
                jsonPath("$.detail") { value("해당 리소스에 접근하기 위한 자격 증명이 없거나 유효하지 않습니다.") }
                jsonPath("$.status") { value(HttpStatus.UNAUTHORIZED.value()) }
                jsonPath("$.instance") { value("/post/$id") }
            }
        }
    }
}