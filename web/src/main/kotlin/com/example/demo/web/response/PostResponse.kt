package com.example.demo.web.response

import com.example.demo.model.Post
import java.time.LocalDateTime

interface PostResponse {
    val id: Long
    val category: CategoryPreview
    val title: String
    val writer: UserView
    val viewCount: Long
    val content: String
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
}

fun PostResponse(
    id: Long,
    category: CategoryPreview,
    title: String,
    writer: UserView,
    viewCount: Long,
    content: String,
    createdAt: LocalDateTime,
    lastUpdatedAt: LocalDateTime,
): PostResponse = PostResponseImpl(
    id = id,
    category = category,
    title = title,
    writer = writer,
    viewCount = viewCount,
    content = content,
    createdAt = createdAt,
    updatedAt = lastUpdatedAt,
)

private data class PostResponseImpl(
    override val id: Long,
    override val category: CategoryPreview,
    override val title: String,
    override val writer: UserView,
    override val viewCount: Long,
    override val content: String,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
) : PostResponse

fun Post.toResponse(): PostResponse {
    return PostResponse(
        id = id,
        category = category.toPreview(),
        title = title,
        writer = writer.toView(),
        viewCount = viewCount,
        content = content,
        createdAt = createdAt,
        lastUpdatedAt = updatedAt,
    )
}