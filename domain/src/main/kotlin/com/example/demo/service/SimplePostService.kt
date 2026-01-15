package com.example.demo.service

import com.example.demo.dto.PostParam
import com.example.demo.exception.BoardErrorCode
import com.example.demo.exception.ErrorCodeException
import com.example.demo.model.Category
import com.example.demo.model.Post
import com.example.demo.model.User
import com.example.demo.repository.CategoryJpaRepository
import com.example.demo.repository.PostJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mapping.PropertyReferenceException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SimplePostService(
    private val categoryJpaRepository: CategoryJpaRepository,
    private val postJpaRepository: PostJpaRepository,
) : PostService {
    var checker = PostChecker {
        if (it.title.isBlank()) {
            throw ErrorCodeException(BoardErrorCode.INVALID_POST_TITLE)
        }
        if (it.content.isBlank()) {
            throw ErrorCodeException(BoardErrorCode.INVALID_POST_CONTENT)
        }
    }

    @Transactional
    override fun create(
        param: PostParam, user: User
    ): Post {
        val category = findByCategory(param.categoryId)
        val post = Post(
            title = param.title,
            writer = user,
            content = param.content,
            category = category,
        )
        checker.check(post)
        return postJpaRepository.save(post)
    }

    @Transactional
    override fun update(
        id: Long, param: PostParam, user: User
    ): Post {
        val post = postJpaRepository.findByIdOrNull(id) ?: run {
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_POST)
        }
        if (post.writer != user) {
            throw ErrorCodeException(BoardErrorCode.NOT_POST_OWNER)
        }
        post {
            this.title = param.title
            this.content = param.content
            this.category = findByCategory(param.categoryId)
        }
        checker.check(post)
        return postJpaRepository.save(post)
    }

    @Transactional
    override fun delete(id: Long, user: User) {
        val post = postJpaRepository.findByIdOrNull(id) ?: run {
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_POST)
        }
        if (post.writer != user) {
            throw ErrorCodeException(BoardErrorCode.NOT_POST_OWNER)
        }
        postJpaRepository.delete(post)
    }

    @Transactional
    override fun findById(id: Long): Post {
        val post = postJpaRepository.findByIdOrNull(id) ?: run {
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_POST)
        }
        post.viewCount++
        return postJpaRepository.save(post)
    }

    @Transactional(readOnly = true)
    override fun findByCategoryId(
        categoryId: Long, pageable: Pageable
    ): Page<Post> {
        if (!categoryJpaRepository.existsById(categoryId)) {
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_CATEGORY)
        }
        try {
            return postJpaRepository.findByCategoryId(categoryId, pageable)
        } catch (_: PropertyReferenceException) {
            throw ErrorCodeException(BoardErrorCode.INVALID_POST_SORT_PROPERTY)
        }
    }

    private fun findByCategory(categoryId: Long): Category {
        return categoryJpaRepository.findByIdOrNull(categoryId) ?: run {
            throw ErrorCodeException(BoardErrorCode.INVALID_POST_CATEGORY)
        }
    }

    fun interface PostChecker {
        fun check(post: Post)
    }
}