package com.example.demo.repository

import com.example.demo.model.Post
import com.example.demo.model.QCategory.category
import com.example.demo.model.QPost.post
import com.example.demo.utils.QueryDsl.orderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

interface PostRepository {
    fun findByCategoryId(categoryId: Long, pageable: Pageable): Page<Post>
}

@Repository
interface PostJpaRepository : PostRepository, JpaRepository<Post, Long>

class PostRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : PostRepository {
    override fun findByCategoryId(
        categoryId: Long,
        pageable: Pageable
    ): Page<Post> {
        // @formatter:off
        val content = queryFactory.selectFrom(post)
            .where(post.category.id.eq(categoryId))
            .innerJoin(post.category, category).fetchJoin()
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .orderBy(*post.orderSpecifier(pageable.sort))
            .fetch()
        return PageableExecutionUtils.getPage(content, pageable) {
            queryFactory.select(post.id.count())
                .from(post)
                .where(post.category.id.eq(categoryId))
                .fetchOne() ?: 0L
        }
        // @formatter:on
    }
}