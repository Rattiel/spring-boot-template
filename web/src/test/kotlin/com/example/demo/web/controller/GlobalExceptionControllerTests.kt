package com.example.demo.web.controller

import com.example.demo.exception.BoardErrorCode
import com.example.demo.web.test.support.errorCode
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hamcrest.Matchers.hasItems
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tools.jackson.databind.ObjectMapper
import java.util.*

@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@Import(GlobalExceptionControllerTests.TestController::class, GlobalExceptionController::class)
@WebMvcTest(
    GlobalExceptionControllerTests.TestController::class,
    properties = [
        "spring.messages.basename=messages, test_message"
    ],
    excludeAutoConfiguration = [
        SecurityAutoConfiguration::class,
    ],
)
class GlobalExceptionControllerTests {
    @RestController
    class TestController {
        data class MessageRequest(
            @field:NotBlank(message = "{message.cannot.be.blank}") @field:NotNull(message = "{message.cannot.be.null}") val message: String?,
        )

        @GetMapping("/unknown-error")
        fun unknownError() {
            throw RuntimeException("Unknown error")
        }

        @PostMapping("/validate-error")
        fun validateError(@RequestBody @Validated request: MessageRequest): MessageRequest {
            return request
        }
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @DisplayName("예상치 못한 예외 발생 시 서버 내부 오류 응답을 반환해야 한다")
    @Test
    fun `should return internal server error response when unexpected exception occurs`() {
        // when & then
        mockMvc.get("/unknown-error") {
            header(HttpHeaders.ACCEPT_LANGUAGE, Locale.KOREAN.language)
        }.andExpect {
            status { isInternalServerError() }

            jsonPath("$.type") { value("http://localhost:8080/docs/index.html#internal-server-error") }
            jsonPath("$.title") { value("알 수 없는 오류") }
            jsonPath("$.detail") { value("알 수 없는 오류가 발생하였습니다.") }
            jsonPath("$.status") { value(HttpStatus.INTERNAL_SERVER_ERROR.value()) }
            jsonPath("$.instance") { value("/unknown-error") }
        }.andDo {
            handle(
                document(
                    "unknown-error",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("type").type(JsonFieldType.STRING).description("에러의 유형을 식별하는 URI 참조입니다."),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("사람이 읽을 수 있는 에러의 간략한 요약입니다."),
                        fieldWithPath("detail").type(JsonFieldType.STRING).description("발생한 HTTP 상태 코드입니다."),
                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("에러 발생 원인에 대한 상세한 설명입니다."),
                        fieldWithPath("instance").type(JsonFieldType.STRING).description("에러가 발생한 구체적인 URI 경로입니다."),
                    ),
                    errorCode<BoardErrorCode>(identifier = "board-error-code"),
                ),
            )
        }
    }

    @DisplayName("유효성 검증 실패 시 필드별 에러 메시지가 포함된 응답을 반환해야 한다")
    @Test
    fun `should return response with field error details when validation fails`() {
        // given
        val request = TestController.MessageRequest(message = null)

        // when & then
        mockMvc.post("/validate-error") {
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
            jsonPath("$.instance") { value("/validate-error") }
            jsonPath("$.errors") {
                value(hasItems(
                    mapOf(
                        "field" to "message",
                        "message" to "메시지는 공백이면 안 됩니다."
                    ),
                    mapOf(
                        "field" to "message",
                        "message" to "메시지는 null이면 안 됩니다."
                    )
                ))
            }
        }.andDo {
            handle(
                document(
                    "validation-error",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("message").ignored()
                    ),
                    responseFields(
                        fieldWithPath("type").ignored(),
                        fieldWithPath("title").ignored(),
                        fieldWithPath("detail").ignored(),
                        fieldWithPath("status").ignored(),
                        fieldWithPath("instance").ignored(),
                        fieldWithPath("errors").type(JsonFieldType.ARRAY).description("유효성 검증에 실패한 필드 객체들의 목록입니다."),
                        fieldWithPath("errors[].field").type(JsonFieldType.STRING).description("검증에 실패한 요청 파라미터 또는 필드명입니다."),
                        fieldWithPath("errors[].message").type(JsonFieldType.STRING).description("해당 필드가 왜 실패했는지에 대한 상세 메시지입니다.")
                    )
                ),
            )
        }
    }
}