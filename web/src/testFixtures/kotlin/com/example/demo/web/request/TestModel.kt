package com.example.demo.web.request

@Suppress("TestFunctionName")
fun CategoryRequest(
    name: String = "test category",
    block: CategoryRequestBuilder.() -> Unit = {},
): CategoryRequest {
    val builder = CategoryRequestBuilder(
        name = name,
    ).apply {
        this.block()
    }
    return CategoryRequest(
        name = builder.name,
    )
}

class CategoryRequestBuilder(
    var name: String,
)

@Suppress("TestFunctionName")
fun PostRequest(
    title: String = "test title",
    content: String = "test content",
    categoryId: Long = 0L,
    block: PostRequestBuilder.() -> Unit = {},
): PostRequest {
    val builder = PostRequestBuilder(
        title = title,
        content = content,
        categoryId = categoryId,
    ).apply {
        this.block()
    }
    return PostRequest(
        title = builder.title,
        content = builder.content,
        categoryId = builder.categoryId,
    )
}

class PostRequestBuilder(
    var title: String,
    var content: String,
    var categoryId: Long,
)