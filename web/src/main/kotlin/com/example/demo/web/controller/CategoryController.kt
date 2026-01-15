package com.example.demo.web.controller

import com.example.demo.dto.CategoryParam
import com.example.demo.model.Category
import com.example.demo.service.CategoryService
import com.example.demo.web.request.CategoryRequest
import com.example.demo.web.response.CategoryResponse
import com.example.demo.web.response.ListResponse
import com.example.demo.web.response.toResponse
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/category")
class CategoryController(
    private val categoryService: CategoryService,
) {
    fun CategoryRequest.toParam(): CategoryParam = CategoryParam(
        name = name,
    )

    fun List<Category>.toResponse(): ListResponse<CategoryResponse> {
        return ListResponse(
            items = map {
                it.toResponse()
            },
        )
    }

    @GetMapping
    fun getAll(): ListResponse<CategoryResponse> {
        val categories = categoryService.findAll()
        return categories.toResponse()
    }

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
    ): CategoryResponse {
        val category = categoryService.findById(id = id)
        return category.toResponse()
    }

    @PostMapping
    fun create(
        @RequestBody @Validated request: CategoryRequest,
    ): CategoryResponse {
        val category = categoryService.create(param = request.toParam())
        return category.toResponse()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Validated request: CategoryRequest,
    ): CategoryResponse {
        val category = categoryService.update(id = id, param = request.toParam())
        return category.toResponse()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: Long,
    ) {
        categoryService.delete(id = id)
    }
}