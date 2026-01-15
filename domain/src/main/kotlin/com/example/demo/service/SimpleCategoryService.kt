package com.example.demo.service

import com.example.demo.dto.CategoryParam
import com.example.demo.exception.BoardErrorCode
import com.example.demo.exception.ErrorCodeException
import com.example.demo.model.Category
import com.example.demo.repository.CategoryJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SimpleCategoryService(
    private val categoryJpaRepository: CategoryJpaRepository,
) : CategoryService {
    var check = CategoryChecker {
        if (it.name.isBlank()) {
            throw ErrorCodeException(BoardErrorCode.INVALID_CATEGORY_NAME)
        }
    }

    @Transactional
    override fun create(param: CategoryParam): Category {
        val category = Category(name = param.name)
        check.check(category)
        return categoryJpaRepository.save(category)
    }

    @Transactional
    override fun update(id: Long, param: CategoryParam): Category {
        val category = categoryJpaRepository.findByIdOrNull(id) ?: run {
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_CATEGORY)
        }
        category {
            this.name = param.name
        }
        check.check(category)
        return categoryJpaRepository.save(category)
    }

    @Transactional
    override fun delete(id: Long) {
        val category = categoryJpaRepository.findByIdOrNull(id) ?: run {
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_CATEGORY)
        }
        categoryJpaRepository.delete(category)
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): Category {
        return categoryJpaRepository.findByIdOrNull(id) ?: run {
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_CATEGORY)
        }
    }

    @Transactional(readOnly = true)
    override fun findAll(): List<Category> {
        return categoryJpaRepository.findAll()
    }

    fun interface CategoryChecker {
        fun check(category: Category)
    }
}