package com.example.demo.utils

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.Expressions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.core.PropertyReferenceException
import org.springframework.data.domain.Sort

class QueryDslTests {
    @Suppress("unused")
    class User(
        val username: String,
        val password: String,
        val age: Int,
        val roles: List<String>,
    )

    val user = EntityPathBase(User::class.java, User::class.simpleName)

    @DisplayName("단일 정렬 조건이 주어지면 단일 정렬 객체를 반환해야 한다")
    @Test
    fun `should return single order specifier when single sort condition is provided`() {
        // given
        val sort = Sort.by(Sort.Direction.DESC, User::username.name)

        // when
        val result = with(QueryDsl) {
            user.orderSpecifier(sort)
        }

        // then
        val expected = listOf(
            OrderSpecifier(Order.DESC, Expressions.stringPath(user, User::username.name)),
        )
        assertIterableEquals(expected, result.toList())
    }

    @DisplayName("다중 정렬 조건이 주어지면 다중 정렬 객체를 반환해야 한다")
    @Test
    fun `should return multiple order specifiers when multiple sort orders are provided`() {
        // given
        val sort = Sort.by(Sort.Order.desc(User::username.name), Sort.Order.asc(User::password.name))

        // when
        val result = with(QueryDsl) {
            user.orderSpecifier(sort)
        }

        // then
        val expected = listOf(
            OrderSpecifier(Order.DESC, Expressions.stringPath(user, User::username.name)),
            OrderSpecifier(Order.ASC, Expressions.stringPath(user, User::password.name)),
        )
        assertIterableEquals(expected, result.toList())
    }

    @DisplayName("Null 처리 옵션이 주어지면 해당 옵션이 적용되어야 한다")
    @Test
    fun `should apply null handling option when provided`() {
        // given
        val sort = Sort.by(Sort.Direction.DESC, User::username.name)
        val nullHandling = OrderSpecifier.NullHandling.NullsFirst

        // when
        val result = with(QueryDsl) {
            user.orderSpecifier(sort, nullHandling)
        }

        // then
        val expected = listOf(
            OrderSpecifier(Order.DESC, Expressions.stringPath(user, User::username.name), nullHandling),
        )
        assertIterableEquals(expected, result.toList())
    }

    @DisplayName("정렬 조건이 비어있으면 빈 배열을 반환해야 한다")
    @Test
    fun `should return empty array when sort is empty`() {
        // given
        val sort = Sort.unsorted()

        // when
        val result = with(QueryDsl) {
            user.orderSpecifier(sort)
        }

        // then
        assertEquals(0, result.size)
    }

    @DisplayName("원시 타입으로 정렬 가능해야 한다")
    @Test
    fun `should support sorting by primitive types`() {
        // given
        val sort = Sort.by(Sort.Direction.ASC, User::age.name)

        // when
        val result = with(QueryDsl) {
            user.orderSpecifier(sort)
        }

        // then
        val expected = listOf(
            OrderSpecifier(Order.ASC, Expressions.numberPath(Int::class.java, user, User::age.name)),
        )
        assertIterableEquals(expected, result.toList())
    }

    @DisplayName("정렬 불가능한 필드로 정렬 시도 시 예외가 발생해야 한다")
    @Test
    fun `should throw exception when sorting by non-comparable field`() {
        // given
        val sort = Sort.by(User::roles.name)

        // when & then
        assertThrows<PropertyReferenceException> {
            with(QueryDsl) {
                user.orderSpecifier(sort)
            }
        }
    }

    @DisplayName("존재하지 않는 필드로 정렬 시도 시 예외가 발생해야 한다")
    @Test
    fun `should throw exception when field does not exist`() {
        // given
        val sort = Sort.by("notExists")

        // when & then
        assertThrows<PropertyReferenceException> {
            with(QueryDsl) {
                user.orderSpecifier(sort)
            }
        }
    }
}