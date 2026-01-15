package com.example.demo.web.core

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail.forStatusAndDetail
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.web.ErrorResponse
import java.net.URI
import java.nio.charset.StandardCharsets

class SecurityExceptionHandler(
    private val objectMapper: ObjectMapper,
    private val messageSource: MessageSource,
) : AccessDeniedHandler, AuthenticationEntryPoint {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        ex: AccessDeniedException,
    ) {
        writeErrorResponse(request, response, ex, HttpStatus.FORBIDDEN)
    }

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        ex: AuthenticationException,
    ) {
        writeErrorResponse(request, response, ex, HttpStatus.UNAUTHORIZED)
    }

    private inline fun <reified T: Exception> writeErrorResponse(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: T,
        status: HttpStatus,
    ) {
        response.status = status.value()
        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()

        val problemDetail = ErrorResponse.builder(exception, forStatusAndDetail(status, exception.message))
            .instance(URI.create(request.requestURI))
            .typeMessageCode("problemDetail.type.${T::class.qualifiedName}")
            .titleMessageCode("problemDetail.title.${T::class.qualifiedName}")
            .detailMessageCode("problemDetail.${T::class.qualifiedName}")
            .build()
            .updateAndGetBody(messageSource, LocaleContextHolder.getLocale())

        objectMapper.writeValue(response.writer, problemDetail)
    }
}