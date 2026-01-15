package com.example.demo.dto

@Suppress("TestFunctionName")
fun PostParam(
    title: String = "test title",
    content: String = "test content",
    categoryId: Long = 0L,
    block: PostParamBuilder.() -> Unit = {},
): PostParam {
    val builder = PostParamBuilder(
        title = title,
        content = content,
        categoryId = categoryId,
    ).apply {
        this.block()
    }
    return PostParam(
        title = builder.title,
        content = builder.content,
        categoryId = builder.categoryId,
    )
}

@Suppress("TestFunctionName")
fun CategoryParam(
    name: String = "test name",
    block: CategoryParam.() -> Unit = {},
): CategoryParam {
    return CategoryParam(
        name = name,
    ).apply {
        this.block()
    }
}

data class PostParamBuilder(
    var title: String,
    var content: String,
    var categoryId: Long,
)