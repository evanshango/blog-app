package com.codewithevans.blog.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import kotlin.collections.HashSet

@Entity
@Table(
    name = "users",
    indexes = [Index(name = "user_indices", columnList = "firstName, lastName, email, createdAt, updatedAt")],
    uniqueConstraints = [UniqueConstraint(columnNames = ["email"])]
)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")
    val id: UUID? = null,
    @Column(nullable = false)
    var firstName: String,
    @Column(nullable = false)
    var lastName: String,
    @Column(nullable = false, unique = true)
    var email: String,
    var emailConfirmed: Boolean? = false,
    var enableNotifications: Boolean? = false,
    var image: String? = null,
    @Column(nullable = false)
    var password: String? = null,
    @Column(nullable = false)
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roles: Set<Role> = HashSet(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var posts: Set<Post> = HashSet(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var comments: Set<Comment> = HashSet()
){
    @PrePersist
    fun generateCreatedAtValue(){
        this.createdAt = LocalDateTime.now()
    }
}
