package com.example.demo.web.response

interface PageResponse<T> {
    val items: List<T>
    val total: Long
}

fun <T> PageResponse(
    items: List<T>,
    total: Long,
): PageResponse<T> = PageResponseImpl(
    items = items,
    total = total,
)

private data class PageResponseImpl<T>(
    override val items: List<T>,
    override val total: Long,
) : PageResponse<T>