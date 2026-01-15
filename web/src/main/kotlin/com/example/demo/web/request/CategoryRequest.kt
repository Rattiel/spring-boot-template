package com.example.demo.web.request

import jakarta.validation.constraints.NotBlank

data class CategoryRequest(
    @field:NotBlank(message = "{category.name.not.blank}") val name: String,
)