package com.example.demo.service

import com.example.demo.dto.CategoryParam
import com.example.demo.exception.BoardErrorCode
import com.example.demo.exception.ErrorCodeException
import com.example.demo.model.Category
import com.example.demo.repository.CategoryJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SimpleCategoryService(
    private val categoryJpaRepository: CategoryJpaRepository,
) : CategoryService {
    private val log = LoggerFactory.getLogger(this::class.java)

    var checker = CategoryChecker { category ->
        if (category.name.isBlank()) {
            log.warn("Invalid Category Name. categoryId={}", category.id)
            throw ErrorCodeException(BoardErrorCode.INVALID_CATEGORY_NAME)
        }
    }

    @Transactional
    override fun create(param: CategoryParam): Category {
        log.info("Creating Category. name={}", param.name)

        val category = Category(name = param.name)
        checker.check(category)

        val savedCategory = categoryJpaRepository.save(category)

        log.info("Category Created. categoryId={}", savedCategory.id)
        return savedCategory
    }

    @Transactional
    override fun update(id: Long, param: CategoryParam): Category {
        log.info("Updating Category. categoryId={}", id)

        val category = categoryJpaRepository.findByIdOrNull(id) ?: run {
            log.warn("Update Failed: Category Not Found. categoryId={}", id)
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_CATEGORY)
        }

        category {
            this.name = param.name
        }

        checker.check(category)

        val updatedCategory = categoryJpaRepository.save(category)
        log.info("Category Updated. categoryId={}", id)

        return updatedCategory
    }

    @Transactional
    override fun delete(id: Long) {
        log.info("Deleting Category. categoryId={}", id)

        val category = categoryJpaRepository.findByIdOrNull(id) ?: run {
            log.warn("Delete Failed: Category Not Found. categoryId={}", id)
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_CATEGORY)
        }

        categoryJpaRepository.delete(category)
        log.info("Category Deleted. categoryId={}", id)
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): Category {
        log.info("Finding Category. categoryId={}", id)

        return categoryJpaRepository.findByIdOrNull(id) ?: run {
            log.warn("Find Failed: Category Not Found. categoryId={}", id)
            throw ErrorCodeException(BoardErrorCode.NOT_FOUND_CATEGORY)
        }
    }

    @Transactional(readOnly = true)
    override fun findAll(): List<Category> {
        log.info("Finding All Categories")

        val categories = categoryJpaRepository.findAll()

        log.info("All Categories Found. size={}", categories.size)

        return categories
    }

    fun interface CategoryChecker {
        fun check(category: Category)
    }
}