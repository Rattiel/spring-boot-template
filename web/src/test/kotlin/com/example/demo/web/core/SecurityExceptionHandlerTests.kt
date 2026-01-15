package com.example.demo.web.core

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.InsufficientAuthenticationException
import java.io.PrintWriter
import java.net.URI
import java.nio.charset.StandardCharsets

class SecurityExceptionHandlerTests {
    val objectMapper = mockk<ObjectMapper>()
    val messageSource = mockk<MessageSource>()
    val request = mockk<HttpServletRequest>()
    val response = mockk<HttpServletResponse>()
    val writer = mockk<PrintWriter>()
    val securityExceptionHandler = SecurityExceptionHandler(objectMapper, messageSource)

    @DisplayName("인증에 실패하면 401 Unauthorized를 반환한다")
    @Test
    fun `should return 401 Unauthorized when authentication fails`() {
        // given
        val exception = InsufficientAuthenticationException("Token is missing")

        every { request.requestURI } returns "/test/api"
        every { response.status = HttpStatus.UNAUTHORIZED.value() } just Runs
        every { response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE } just Runs
        every { response.characterEncoding = StandardCharsets.UTF_8.name() } just Runs
        every { response.writer } returns writer
        every { objectMapper.writeValue(any<PrintWriter>(), any()) } just Runs
        every { messageSource.getMessage(any(), any(), any(), any()) } returns "Unauthorized"

        // when
        securityExceptionHandler.commence(request, response, exception)

        // then
        verify { response.status = HttpStatus.UNAUTHORIZED.value() }
        verify { response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE }
        verify { response.characterEncoding = StandardCharsets.UTF_8.name() }

        val problemDetailSlot = slot<ProblemDetail>()
        verify { objectMapper.writeValue(writer, capture(problemDetailSlot)) }

        val capturedDetail = problemDetailSlot.captured

        assertEquals(HttpStatus.UNAUTHORIZED.value(), capturedDetail.status)
        assertEquals(URI.create("/test/api"), capturedDetail.instance)
        assertEquals("Unauthorized", capturedDetail.title)
        assertEquals("Unauthorized", capturedDetail.detail)
        assertEquals(URI.create("Unauthorized"), capturedDetail.type)
    }

    @DisplayName("권한이 부족하면 403 Forbidden을 반환한다")
    @Test
    fun `should return 403 Forbidden when access is denied`() {
        // given
        val exception = AccessDeniedException("Access is denied")

        every { request.requestURI } returns "/test/api"
        every { response.status = HttpStatus.FORBIDDEN.value() } just Runs
        every { response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE } just Runs
        every { response.characterEncoding = StandardCharsets.UTF_8.name() } just Runs
        every { response.writer } returns writer
        every { objectMapper.writeValue(any<PrintWriter>(), any()) } just Runs
        every { messageSource.getMessage(any(), any(), any(), any()) } returns "Forbidden"

        // when
        securityExceptionHandler.handle(request, response, exception)

        // then
        verify { response.status = HttpStatus.FORBIDDEN.value() }
        verify { response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE }
        verify { response.characterEncoding = StandardCharsets.UTF_8.name() }

        val problemDetailSlot = slot<ProblemDetail>()
        verify { objectMapper.writeValue(writer, capture(problemDetailSlot)) }

        val capturedDetail = problemDetailSlot.captured

        assertEquals(HttpStatus.FORBIDDEN.value(), capturedDetail.status)
        assertEquals(URI.create("/test/api"), capturedDetail.instance)
        assertEquals("Forbidden", capturedDetail.title)
        assertEquals("Forbidden", capturedDetail.detail)
        assertEquals(URI.create("Forbidden"), capturedDetail.type)
    }
}