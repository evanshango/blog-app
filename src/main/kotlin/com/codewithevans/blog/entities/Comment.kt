package com.codewithevans.blog.entities

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "comments",
    indexes = [Index(name = "comment_indices", columnList = "comment, user_id, createdAt, updatedAt")]
)
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")
    val id: UUID? = null,
    @Column(nullable = false)
    var comment: String,
    @Column(nullable = false)
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    var post: Post? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null
) {
    @PrePersist
    fun createdAtValue() {
        this.createdAt = LocalDateTime.now()
    }
}
