package com.codewithevans.blog.entities

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

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
    @Column(nullable = false)
    var password: String? = null,
    @Column(nullable = false)
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
    var passwordExpiry: LocalDateTime? = null,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roles: Set<Role>? = HashSet(),
) {
    @PrePersist
    fun generateCreatedAtValue() {
        this.createdAt = LocalDateTime.now()
    }
}
