package com.example.demo.web.response

import com.example.demo.model.Post
import java.time.LocalDateTime

interface PostPreview {
    val id: Long
    val title: String
    val writer: UserView
    val createdAt: LocalDateTime
}

fun PostPreview(
    id: Long,
    title: String,
    writer: UserView,
    createdAt: LocalDateTime,
): PostPreview = PostPreviewImpl(
    id = id, title = title, writer = writer, createdAt = createdAt
)

private data class PostPreviewImpl(
    override val id: Long,
    override val title: String,
    override val writer: UserView,
    override val createdAt: LocalDateTime,
) : PostPreview

fun Post.toPreview(): PostPreview {
    return PostPreview(
        id = id,
        title = title,
        writer = writer.toView(),
        createdAt = createdAt,
    )
}