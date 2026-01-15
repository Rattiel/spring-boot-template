package com.example.demo.web.response

import com.example.demo.model.Category
import java.time.LocalDateTime

interface CategoryResponse {
    val id: Long
    val name: String
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
}

fun CategoryResponse(
    id: Long,
    name: String,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
): CategoryResponse = CategoryResponseImpl(
    id = id,
    name = name,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private data class CategoryResponseImpl(
    override val id: Long,
    override val name: String,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
) : CategoryResponse

fun Category.toResponse(): CategoryResponse {
    return CategoryResponse(
        id = id,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}