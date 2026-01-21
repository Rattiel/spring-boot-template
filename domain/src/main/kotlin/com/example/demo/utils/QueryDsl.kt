package com.example.demo.utils

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.PathBuilder
import org.springframework.data.core.PropertyPath
import org.springframework.data.core.PropertyReferenceException
import org.springframework.data.core.TypeInformation
import org.springframework.data.domain.Sort

object QueryDsl {
    @Suppress("UNCHECKED_CAST")
    fun EntityPathBase<*>.orderSpecifier(
        sort: Sort,
        nullHandling: OrderSpecifier.NullHandling = OrderSpecifier.NullHandling.Default,
    ): Array<OrderSpecifier<*>> {
        val pathBuilder = PathBuilder(this.type, this.metadata)
        return sort.map { order ->
            val propertyPath = PropertyPath.from(order.property, this.type)
            val leafPropertyType = propertyPath.leafProperty.typeInformation.type
            if (!leafPropertyType.isPrimitive && !Comparable::class.java.isAssignableFrom(leafPropertyType)) {
                throw PropertyReferenceException(order.property, TypeInformation.of(this.type), emptyList())
            }
            val direction = when (order.direction) {
                Sort.Direction.ASC -> Order.ASC
                Sort.Direction.DESC -> Order.DESC
            }
            val expression = pathBuilder.get(order.property) as Expression<Comparable<*>>
            return@map OrderSpecifier(direction, expression, nullHandling)
        }.toList().toTypedArray()
    }
}