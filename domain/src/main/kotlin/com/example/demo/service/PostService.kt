package com.example.demo.service

import com.example.demo.dto.PostParam
import com.example.demo.model.Post
import com.example.demo.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostService {
    fun create(param: PostParam, user: User): Post

    fun update(id: Long, param: PostParam, user: User): Post

    fun delete(id: Long, user: User)

    fun findById(id: Long): Post

    fun findByCategoryId(categoryId: Long, pageable: Pageable): Page<Post>
}