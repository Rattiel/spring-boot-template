package com.example.demo.web.controller

import com.example.demo.dto.PostParam
import com.example.demo.model.Post
import com.example.demo.model.User
import com.example.demo.service.PostService
import com.example.demo.web.core.AuthenticationUser
import com.example.demo.web.request.PostRequest
import com.example.demo.web.response.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/post")
class PostController(
    private val postService: PostService,
) {
    fun PostRequest.toParam(): PostParam = PostParam(
        title = title,
        content = content,
        categoryId = categoryId,
    )

    fun Page<Post>.toResponse(): PageResponse<PostPreview> {
        return PageResponse(
            items = content.map {
                it.toPreview()
            },
            total = totalElements,
        )
    }

    @GetMapping
    fun getAll(
        @RequestParam categoryId: Long,
        @PageableDefault pageable: Pageable,
    ): PageResponse<PostPreview> {
        val page = postService.findByCategoryId(categoryId = categoryId, pageable = pageable)
        return page.toResponse()
    }

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
    ): PostResponse {
        val category = postService.findById(id = id)
        return category.toResponse()
    }

    @PostMapping
    fun create(
        @RequestBody @Validated request: PostRequest,
        @AuthenticationUser user: User,
    ): PostResponse {
        val post = postService.create(param = request.toParam(), user = user)
        return post.toResponse()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Validated request: PostRequest,
        @AuthenticationUser user: User,
    ): PostResponse {
        val post = postService.update(id = id, param = request.toParam(), user = user)
        return post.toResponse()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: Long,
        @AuthenticationUser user: User,
    ) {
        postService.delete(id = id, user = user)
    }
}