package com.example.demo.web.response

import com.example.demo.model.Category

interface CategoryPreview {
    val id: Long
    val name: String
}

fun CategoryPreview(
    id: Long,
    name: String,
): CategoryPreview = CategoryPreviewImpl(
    id = id,
    name = name,
)

private data class CategoryPreviewImpl(
    override val id: Long,
    override val name: String,
) : CategoryPreview

fun Category.toPreview(): CategoryPreview {
    return CategoryPreview(
        id = id,
        name = name,
    )
}