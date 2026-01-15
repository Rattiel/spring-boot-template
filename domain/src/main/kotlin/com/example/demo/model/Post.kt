package com.example.demo.model

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "post")
class Post(
    // @formatter:off
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    var category: Category,

    @Column(nullable = false)
    var title: String,

    @Embedded
    @AttributeOverride(name = "id", column = Column(name = "writer_id", nullable = false, updatable = false))
    val writer: User,
    
    @Column(nullable = false, length = 4096)
    var content: String,
    // @formatter:on
) : AuditableEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post-sequence")
    @SequenceGenerator(name = "post-sequence", sequenceName = "post_sequence", allocationSize = 50)
    @Column(nullable = false, unique = true, updatable = false)
    var id: Long = 0L
        internal set

    @Column(nullable = false)
    var viewCount: Long = 0

    operator fun invoke(block: Post.() -> Unit) {
        this.block()
    }
}