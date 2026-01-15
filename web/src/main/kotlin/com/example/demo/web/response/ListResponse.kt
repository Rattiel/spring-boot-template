package com.example.demo.web.response

interface ListResponse<T> {
    val items: List<T>
}

fun <T> ListResponse(
    items: List<T>,
): ListResponse<T> = ListResponseImpl(
    items = items,
)

private data class ListResponseImpl<T>(
    override val items: List<T>,
) : ListResponse<T>