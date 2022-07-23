package com.codewithevans.blog.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "posts",
    indexes = [Index(name = "post_indices", columnList = "title, user_id, createdAt, updatedAt")],
    uniqueConstraints = [UniqueConstraint(columnNames = ["title"])]
)
data class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")
    val id: UUID? = null,
    @Column(nullable = false)
    var title: String,
    @Column(nullable = false)
    var slug: String? = null,
    @Column(nullable = false)
    var content: String,
    var approved: Boolean? = false,
    @Column(nullable = false)
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    var comments: Set<Comment> = HashSet()
){
    @PrePersist
    fun generateCreatedAtAndSlugValues() {
        this.slug = title.lowercase().replace(" ", "-")
        this.createdAt = LocalDateTime.now()
    }
}
