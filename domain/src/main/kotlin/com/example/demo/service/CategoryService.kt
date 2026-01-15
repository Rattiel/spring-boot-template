package com.example.demo.service

import com.example.demo.dto.CategoryParam
import com.example.demo.model.Category

interface CategoryService {
    fun create(param: CategoryParam): Category

    fun update(id: Long, param: CategoryParam): Category

    fun delete(id: Long)

    fun findById(id: Long): Category

    fun findAll(): List<Category>
}