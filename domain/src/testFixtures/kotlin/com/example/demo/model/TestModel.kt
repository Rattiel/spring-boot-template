package com.example.demo.model

import java.time.LocalDateTime

@Suppress("TestFunctionName")
fun Category(
    id: Long = 0L,
    name: String = "test name",
    createdAt: LocalDateTime = LocalDateTime.now(),
    lastUpdatedAt: LocalDateTime = LocalDateTime.now(),
    block: Category.() -> Unit = {},
): Category {
    return Category(
        name = name,
    ).apply {
        this.block()
        this.id = id
        this.createdAt = createdAt
        this.updatedAt = lastUpdatedAt
    }
}

@Suppress("TestFunctionName")
fun Post(
    id: Long = 0L,
    category: Category = Category(),
    title: String = "test title",
    writer: User = User(),
    viewCount: Long = 0,
    content: String = "test content",
    createdAt: LocalDateTime = LocalDateTime.now(),
    lastUpdatedAt: LocalDateTime = LocalDateTime.now(),
    block: Post.() -> Unit = {},
): Post {
    return Post(
        category = category,
        title = title,
        writer = writer,
        content = content,
    ).apply {
        this.block()
        this.id = id
        this.viewCount = viewCount
        this.createdAt = createdAt
        this.updatedAt = lastUpdatedAt
    }
}

@Suppress("TestFunctionName")
fun User(
    id: String = "test user",
    block: User.() -> Unit = {},
): User {
    return User(id = id).apply {
        this.block()
    }
}