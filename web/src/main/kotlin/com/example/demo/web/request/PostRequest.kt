package com.example.demo.web.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class PostRequest(
    @field:NotBlank(message = "{post.title.not.blank}") val title: String,
    @field:NotBlank(message = "{post.content.not.blank}") val content: String,
    @field:NotNull(message = "{post.categoryId.not.null}") val categoryId: Long,
)