package com.example.demo.service

import com.example.demo.dto.PostParam
import com.example.demo.exception.BoardErrorCode
import com.example.demo.exception.ErrorCodeException
import com.example.demo.model.Category
import com.example.demo.model.Post
import com.example.demo.model.User
import com.example.demo.repository.CategoryJpaRepository
import com.example.demo.repository.PostJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.data.core.PropertyReferenceException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SimplePostService(
    private val categoryJpaRepository: CategoryJpaRepository,
    private val postJpaRepository: PostJpaRepository,
) : PostService {
    private val log = LoggerFactory.getLogger(this::class.java)

    var checker = PostChecker { post ->
        if (post.title.isBlank()) {
            log.warn("Invalid Post Title. postId={}, userId={}", post.id, post.writer.id)
            throw ErrorCodeException(BoardErrorCode.INVALID_POST_TITLE)
        }
        if (post.content.isBlank()) {
            log.warn("Invalid Post Content. postId={}, userId={}", post.id, post.writer.id)
            throw ErrorCodeException(BoardErrorCode.INVALID_POST_CONTENT)
        }
    }

    @Transactional
    override fun create(
        param: PostParam, user: User
    ): Post {
        log.info("Creating Post. userId={}, title={}", user.id, param.title)

        val category = findByCategory(param.categoryId)
        val post = Post(
            title = param.title,
            writer = user,
            content = param.content,
            category = category,
        )
        checker.check(post)

        val savedPost = postJpaRepository.save(post)

        log.info("Post Created. postId={}", savedPost.id)

        return savedPost
    }

    @Transactional
    override fun update(
        id: Long, param: PostParam, user: User
    ): Post {
        log.info("Updating Post. postId={}, userId={}", id, user.id)

        val post = postJpaRepository.findByIdOrNull(id) ?: run {
            log.warn("Update Failed: Post Not Found. postId={}", id)
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_POST)
        }

        if (post.writer != user) {
            log.warn("Update Denied: Not Owner. postId={}, userId={}", id, user.id)
            throw ErrorCodeException(BoardErrorCode.NOT_POST_OWNER)
        }

        post {
            this.title = param.title
            this.content = param.content
            this.category = findByCategory(param.categoryId)
        }

        checker.check(post)

        val updatedPost = postJpaRepository.save(post)
        log.info("Post Updated. postId={}", id)

        return updatedPost
    }

    @Transactional
    override fun delete(id: Long, user: User) {
        log.info("Deleting Post. postId={}, userId={}", id, user.id)

        val post = postJpaRepository.findByIdOrNull(id) ?: run {
            log.warn("Delete Failed: Post Not Found. postId={}", id)
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_POST)
        }

        if (post.writer != user) {
            log.warn("Delete Denied: Not Owner. postId={}, userId={}", id, user.id)
            throw ErrorCodeException(BoardErrorCode.NOT_POST_OWNER)
        }

        postJpaRepository.delete(post)
        log.info("Post Deleted. postId={}", id)
    }

    @Transactional
    override fun findById(id: Long): Post {
        log.info("Finding Post. postId={}", id)

        val post = postJpaRepository.findByIdOrNull(id) ?: run {
            log.warn("Find Failed: Post Not Found. postId={}", id)
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_POST)
        }

        post.viewCount++

        return postJpaRepository.save(post)
    }

    @Transactional(readOnly = true)
    override fun findByCategoryId(
        categoryId: Long, pageable: Pageable
    ): Page<Post> {
        log.info("Finding Posts by Category. categoryId={}, page={}", categoryId, pageable.pageNumber)

        if (!categoryJpaRepository.existsById(categoryId)) {
            log.warn("Find Posts Failed: Category Not Found. categoryId={}", categoryId)
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_CATEGORY)
        }

        try {
            return postJpaRepository.findByCategoryId(categoryId, pageable)
        } catch (e: PropertyReferenceException) {
            log.warn("Invalid Sort Property. categoryId={}, property={}", categoryId, e.propertyName)
            throw ErrorCodeException(BoardErrorCode.INVALID_POST_SORT_PROPERTY)
        }
    }

    private fun findByCategory(categoryId: Long): Category {
        return categoryJpaRepository.findByIdOrNull(categoryId) ?: run {
            log.warn("Category Validation Failed. categoryId={}", categoryId)
            throw ErrorCodeException(BoardErrorCode.INVALID_POST_CATEGORY)
        }
    }

    fun interface PostChecker {
        fun check(post: Post)
    }
}