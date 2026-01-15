package com.example.demo.web.test.support

import com.example.demo.exception.ErrorCode
import com.example.demo.web.controller.status
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.operation.Operation
import org.springframework.restdocs.operation.OperationRequest
import org.springframework.restdocs.operation.OperationRequestFactory
import org.springframework.restdocs.operation.OperationResponse
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders
import org.springframework.restdocs.snippet.TemplatedSnippet
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Locale.getDefault

fun withBearerToken(): HeadersModifyingOperationPreprocessor {
    return modifyHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer <ACCESS_TOKEN>")
}

fun uriDecode(): OperationPreprocessor {
    return UriDecodePreprocessor()
}

class UriDecodePreprocessor : OperationPreprocessor {
    private val requestFactory = OperationRequestFactory()

    override fun preprocess(request: OperationRequest): OperationRequest {
        // @formatter:off
        val decodedUri = UriComponentsBuilder.fromUri(request.uri)
            .replaceQuery(URLDecoder.decode(request.uri.query, StandardCharsets.UTF_8))
            .build(true)
            .toUri()
        // @formatter:on

        return requestFactory.create(
            decodedUri, request.method, request.content, request.headers, request.parts,
        )
    }

    override fun preprocess(response: OperationResponse): OperationResponse {
        return response
    }
}

inline fun <reified T> errorCode(
    templateName: String = "error-code",
    identifier: String = templateName,
    attributes: Map<String, Any> = emptyMap(),
): ErrorCodeSnippet<T> where T : Enum<T>, T : ErrorCode {
    return ErrorCodeSnippet(
        enumClass = T::class.java,
        identifier = identifier,
        templateName = templateName,
        attributes = attributes,
    )
}

class ErrorCodeSnippet<T>(
    private val enumClass: Class<T>,
    identifier: String,
    templateName: String,
    attributes: Map<String, Any> = emptyMap(),
) : TemplatedSnippet(identifier, templateName, attributes) where T : Enum<T>, T : ErrorCode {
    override fun createModel(operation: Operation): Map<String, Any> {
        val fields = enumClass.enumConstants.map {
            buildMap {
                put("status", it.status.value())
                put("name", it.name.lowercase(getDefault()).replace("_", "-"))
                put("message", it.message)
            }
        }
        return mapOf("fields" to fields)
    }
}