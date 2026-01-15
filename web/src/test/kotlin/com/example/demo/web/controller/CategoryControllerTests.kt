package com.example.demo.web.controller

import com.example.demo.model.Category
import com.example.demo.service.CategoryService
import com.example.demo.web.config.TestSecurityConfig
import com.example.demo.web.config.WebConfig
import com.example.demo.web.request.CategoryRequest
import com.example.demo.web.test.support.WithJwt
import com.example.demo.web.test.support.withBearerToken
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.*
import java.util.*

@AutoConfigureRestDocs
@Import(WebConfig::class, TestSecurityConfig::class, GlobalExceptionController::class)
@WebMvcTest(CategoryController::class)
class CategoryControllerTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var categoryService: CategoryService

    @DisplayName("생성 API")
    @Nested
    inner class CreateTests {
        @DisplayName("입력값이 유효하면 카테고리를 생성하고 200 OK를 반환한다")
        @Test
        @WithJwt(scope = "category:write")
        fun `should create category and return 200 OK when input is valid`() {
            // given
            val request = CategoryRequest()
            val category = Category()

            every { categoryService.create(any()) } returns category

            // when & then
            mockMvc.post("/category") {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
            }.andDo {
                handle(
                    document(
                        "category-create",
                        preprocessRequest(prettyPrint(), withBearerToken()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 인증 토큰")
                        ),
                        requestFields(
                            fieldWithPath("name").type(JsonFieldType.STRING).description("생성할 카테고리 이름"),
                        ),
                        responseFields(
                            fieldWithPath("id").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                            fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름"),
                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                            fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 일시")
                        )
                    )
                )
            }

            verify(exactly = 1) {
                categoryService.create(
                    param = match { it.name == request.name },
                )
            }
        }

        @DisplayName("입력값이 유효하지 않으면 400 Bad Request를 반환한다")
        @Test
        @WithJwt(scope = "category:write")
        fun `should return 400 Bad Request when input is invalid`() {
            // given
            val request = CategoryRequest(name = "")

            // when & then
            mockMvc.post("/category") {
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
                jsonPath("$.instance") { value("/category") }
                jsonPath("$.errors[0].field") { value("name") }
                jsonPath("$.errors[0].message") { value("카테고리 이름은 공백일 수 없습니다.") }
            }
        }

        @DisplayName("인증 안된 요청이면 401 Unauthorized를 반환한다")
        @Test
        fun `should return 401 Unauthorized when request is unauthenticated`() {
            // given
            val request = CategoryRequest()

            // when & then
            mockMvc.post("/category") {
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
                jsonPath("$.instance") { value("/category") }
            }
        }

        @DisplayName("권한이 없는 요청이면 403 Forbidden를 반환한다")
        @Test
        @WithJwt
        fun `should return 403 Unauthorized when request is forbidden`() {
            // given
            val request = CategoryRequest()

            // when & then
            mockMvc.post("/category") {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header(HttpHeaders.ACCEPT_LANGUAGE, Locale.KOREAN.language)
            }.andExpect {
                status { isForbidden() }

                jsonPath("$.type") { value("http://localhost:8080/docs/index.html#forbidden") }
                jsonPath("$.title") { value("권한 없음") }
                jsonPath("$.detail") { value("해당 리소스에 접근할 권한이 없습니다.") }
                jsonPath("$.status") { value(HttpStatus.FORBIDDEN.value()) }
                jsonPath("$.instance") { value("/category") }
            }
        }
    }

    @DisplayName("조회 API")
    @Nested
    inner class ReadTests {
        @DisplayName("ID에 해당하는 카테고리가 존재하면 조회하고 200 OK를 반환한다")
        @Test
        fun `should return category and 200 OK when category exists`() {
            // given
            val id = 1L
            val category = Category(id = id)

            every { categoryService.findById(any()) } returns category

            // when & then
            mockMvc.get("/category/{id}", id) {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
            }.andDo {
                handle(
                    document(
                        "category-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("id").description("조회할 카테고리 ID")
                        ),
                        responseFields(
                            fieldWithPath("id").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                            fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름"),
                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                            fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 일시")
                        )
                    )
                )
            }

            verify(exactly = 1) { categoryService.findById(id) }
        }

        @DisplayName("전체 카테고리 목록을 조회하고 200 OK를 반환한다")
        @Test
        fun `should return all categories and 200 OK`() {
            // given
            val categories = listOf(
                Category(id = 1L),
                Category(id = 2L),
            )

            every { categoryService.findAll() } returns categories

            // when & then
            mockMvc.get("/category") {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
            }.andDo {
                handle(
                    document(
                        "category-get-all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                            fieldWithPath("items").type(JsonFieldType.ARRAY).description("카테고리 목록"),
                            fieldWithPath("items[].id").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                            fieldWithPath("items[].name").type(JsonFieldType.STRING).description("카테고리 이름"),
                            fieldWithPath("items[].createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                            fieldWithPath("items[].updatedAt").type(JsonFieldType.STRING).description("수정 일시")
                        )
                    )
                )
            }

            verify(exactly = 1) { categoryService.findAll() }
        }
    }

    @DisplayName("수정 API")
    @Nested
    inner class UpdateTests {
        @DisplayName("입력값이 유효하면 카테고리를 수정하고 200 OK를 반환한다")
        @Test
        @WithJwt(scope = "category:write")
        fun `should update category and return 200 OK when input is valid`() {
            // given
            val id = 1L
            val request = CategoryRequest()
            val category = Category(id = id)

            every { categoryService.update(any(), any()) } returns category

            // when & then
            mockMvc.put("/category/{id}", id) {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
            }.andDo {
                handle(
                    document(
                        "category-update",
                        preprocessRequest(prettyPrint(), withBearerToken()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("id").description("수정할 카테고리 ID")
                        ),
                        requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 인증 토큰")
                        ),
                        requestFields(
                            fieldWithPath("name").type(JsonFieldType.STRING).description("수정할 카테고리 이름"),
                        ),
                        responseFields(
                            fieldWithPath("id").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                            fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름"),
                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                            fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정 일시")
                        )
                    )
                )
            }

            verify(exactly = 1) {
                categoryService.update(
                    id = id,
                    param = match { it.name == request.name },
                )
            }
        }

        @DisplayName("입력값이 유효하지 않으면 400 Bad Request를 반환한다")
        @Test
        @WithJwt(scope = "category:write")
        fun `should return 400 Bad Request when input is invalid`() {
            // given
            val id = 1L
            val request = CategoryRequest(name = "")

            // when & then
            mockMvc.put("/category/{id}", id) {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header(HttpHeaders.ACCEPT_LANGUAGE, Locale.KOREAN.language)
            }.andExpect {
                status { isBadRequest() }

                jsonPath("$.type") { value("http://localhost:8080/docs/index.html#validation-error") }
                jsonPath("$.title") { value("잘못된 요청") }
                jsonPath("$.errors[0].message") { value("카테고리 이름은 공백일 수 없습니다.") }
            }
        }

        @DisplayName("인증 안된 요청이면 401 Unauthorized를 반환한다")
        @Test
        fun `should return 401 Unauthorized when request is unauthenticated`() {
            // given
            val id = 1L
            val request = CategoryRequest(name = "")

            // when & then
            mockMvc.put("/category/{id}", id) {
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
                jsonPath("$.instance") { value("/category/$id") }
            }
        }

        @DisplayName("권한이 없는 요청이면 403 Forbidden를 반환한다")
        @Test
        @WithJwt
        fun `should return 403 Unauthorized when request is forbidden`() {
            // given
            val id = 1L
            val request = CategoryRequest(name = "")

            // when & then
            mockMvc.put("/category/{id}", id) {
                accept = MediaType.APPLICATION_JSON
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header(HttpHeaders.ACCEPT_LANGUAGE, Locale.KOREAN.language)
            }.andExpect {
                status { isForbidden() }

                jsonPath("$.type") { value("http://localhost:8080/docs/index.html#forbidden") }
                jsonPath("$.title") { value("권한 없음") }
                jsonPath("$.detail") { value("해당 리소스에 접근할 권한이 없습니다.") }
                jsonPath("$.status") { value(HttpStatus.FORBIDDEN.value()) }
                jsonPath("$.instance") { value("/category/$id") }
            }
        }
    }

    @DisplayName("삭제 API")
    @Nested
    inner class DeleteTests {
        @DisplayName("카테고리 삭제에 성공하면 204 No Content를 반환한다")
        @Test
        @WithJwt(scope = "category:write")
        fun `should delete category and return 204 No Content`() {
            // given
            val id = 1L
            justRun { categoryService.delete(any()) }

            // when & then
            mockMvc.delete("/category/{id}", id).andExpect {
                status { isNoContent() }
            }.andDo {
                handle(
                    document(
                        "category-delete",
                        preprocessRequest(prettyPrint(), withBearerToken()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("id").description("삭제할 카테고리 ID")
                        ),
                        requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("사용자 인증 토큰")
                        )
                    )
                )
            }

            verify(exactly = 1) { categoryService.delete(id) }
        }

        @DisplayName("인증 안된 요청이면 401 Unauthorized를 반환한다")
        @Test
        fun `should return 401 Unauthorized when request is unauthenticated`() {
            // given
            val id = 1L
            justRun { categoryService.delete(any()) }

            // when & then
            mockMvc.delete("/category/{id}", id) {
                header(HttpHeaders.ACCEPT_LANGUAGE, Locale.KOREAN.language)
            }.andExpect {
                status { isUnauthorized() }

                jsonPath("$.type") { value("http://localhost:8080/docs/index.html#unauthorized") }
                jsonPath("$.title") { value("인증 필요") }
                jsonPath("$.detail") { value("해당 리소스에 접근하기 위한 자격 증명이 없거나 유효하지 않습니다.") }
                jsonPath("$.status") { value(HttpStatus.UNAUTHORIZED.value()) }
                jsonPath("$.instance") { value("/category/$id") }
            }
        }

        @DisplayName("권한이 없는 요청이면 403 Forbidden를 반환한다")
        @Test
        @WithJwt
        fun `should return 403 Unauthorized when request is forbidden`() {
            // given
            val id = 1L
            justRun { categoryService.delete(any()) }

            // when & then
            mockMvc.delete("/category/{id}", id) {
                header(HttpHeaders.ACCEPT_LANGUAGE, Locale.KOREAN.language)
            }.andExpect {
                status { isForbidden() }

                jsonPath("$.type") { value("http://localhost:8080/docs/index.html#forbidden") }
                jsonPath("$.title") { value("권한 없음") }
                jsonPath("$.detail") { value("해당 리소스에 접근할 권한이 없습니다.") }
                jsonPath("$.status") { value(HttpStatus.FORBIDDEN.value()) }
                jsonPath("$.instance") { value("/category/$id") }
            }
        }
    }
}