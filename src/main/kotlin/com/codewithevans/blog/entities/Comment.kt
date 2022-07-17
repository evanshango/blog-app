package com.codewithevans.blog.entities

import com.codewithevans.blog.dtos.AuthorDto
import com.codewithevans.blog.dtos.CommentDto
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
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    var post: Post? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: BlogUser? = null
) {
    @PrePersist
    fun createdAtValue() {
        this.createdAt = LocalDateTime.now()
    }
}

fun Comment.toCommentDto(): CommentDto {
    return CommentDto(
        id = id.toString(),
        comment = comment,
        author = AuthorDto(
            id = user!!.id.toString(),
            firstName = user!!.firstName,
            lastName = user!!.lastName,
            email = user!!.email
        ),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
