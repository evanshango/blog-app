package com.codewithevans.blog.entities

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "replies",
    indexes = [Index(name = "reply_indices", columnList = "reply, user_id, createdAt, updatedAt")]
)
data class Reply(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")
    val id: UUID? = null,
    @Column(nullable = false)
    var reply: String,
    @Column(nullable = false)
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    var comment: Comment? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null
) {
    @PrePersist
    fun createdAtValue() {
        this.createdAt = LocalDateTime.now()
    }
}
