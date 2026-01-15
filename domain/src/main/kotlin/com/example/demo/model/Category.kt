package com.example.demo.model

import jakarta.persistence.*

@Entity
@Table(name = "category")
class Category(
    // @formatter:off
    @Column(nullable = false)
    var name: String,
    // @formatter:on
) : AuditableEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category-sequence")
    @SequenceGenerator(name = "category-sequence", sequenceName = "category_sequence", allocationSize = 1)
    @Column(nullable = false, unique = true, updatable = false)
    var id: Long = 0L
        internal set

    operator fun invoke(block: Category.() -> Unit) {
        this.block()
    }
}