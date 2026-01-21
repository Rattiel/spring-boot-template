package com.example.demo.web.controller

import com.example.demo.exception.BoardErrorCode
import com.example.demo.exception.ErrorCode
import com.example.demo.exception.ErrorCodeException
import com.example.demo.exception.GlobalErrorCode
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.util.WebUtils

@ControllerAdvice(
    annotations = [Controller::class, RestController::class],
)
class GlobalExceptionController(
    messageSource: MessageSource,
) : ResponseEntityExceptionHandler() {
    private val log = LoggerFactory.getLogger(this::class.java)

    init {
        setMessageSource(messageSource)
    }

    @ExceptionHandler(ErrorCodeException::class)
    fun handleErrorCode(ex: ErrorCodeException, request: WebRequest): ResponseEntity<Any> {
        var builder = ErrorResponse.builder(ex, ex.errorCode.status, ex.errorCode.message)
            .typeMessageCode("problemDetail.type.${ex.errorCode.name}")
            .titleMessageCode("problemDetail.title.${ex.errorCode.name}")
            .detailMessageCode("problemDetail.${ex.errorCode.name}")
        ex.args?.let {
            builder = builder.detailMessageArguments(*it)
        }
        val body = builder.build().updateAndGetBody(this.messageSource, LocaleContextHolder.getLocale())
        return createResponseEntity(body, HttpHeaders.EMPTY, ex.errorCode.status, request)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        log.error("Unexpected Error occurred", ex)
        return handleErrorCode(ErrorCodeException(GlobalErrorCode.UNKNOWN_ERROR), request).also {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST)
        }
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest,
    ): ResponseEntity<Any>? {
        val body = ex.updateAndGetBody(messageSource, LocaleContextHolder.getLocale()).also {
            val validationErrors = ex.bindingResult.fieldErrors.map { error ->
                ValidationError(field = error.field, message = error.defaultMessage)
            }
            it.setProperty("errors", validationErrors)
        }
        return handleExceptionInternal(ex, body, headers, status, request)
    }

    data class ValidationError(val field: String, val message: String?)
}

val ErrorCode.status: HttpStatus
    get() = when (this) {
        GlobalErrorCode.UNKNOWN_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR

        BoardErrorCode.INVALID_CATEGORY_NAME -> HttpStatus.FORBIDDEN
        BoardErrorCode.INVALID_POST_TITLE -> HttpStatus.BAD_REQUEST
        BoardErrorCode.INVALID_POST_CONTENT -> HttpStatus.FORBIDDEN
        BoardErrorCode.INVALID_POST_CATEGORY -> HttpStatus.FORBIDDEN
        BoardErrorCode.INVALID_POST_SORT_PROPERTY -> HttpStatus.FORBIDDEN
        BoardErrorCode.NOT_POST_OWNER -> HttpStatus.FORBIDDEN

        BoardErrorCode.NOT_FOUND_CATEGORY -> HttpStatus.NOT_FOUND
        BoardErrorCode.NOT_FOUND_POST -> HttpStatus.NOT_FOUND
    }