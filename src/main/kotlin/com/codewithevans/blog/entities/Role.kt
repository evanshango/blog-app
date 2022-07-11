package com.codewithevans.blog.entities

import com.codewithevans.blog.dtos.RoleDto
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "roles",
    indexes = [Index(name = "role_indices", columnList = "name")],
    uniqueConstraints = [UniqueConstraint(columnNames = ["name"])]
)
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")
    val id: UUID? = null,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var description: String,
    @Column(nullable = false)
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
){
    @PrePersist
    fun generateCreatedAtValue(){
        this.createdAt = LocalDateTime.now()
    }
}

fun Role.toRoleDto(): RoleDto{
    return RoleDto(
        id = id.toString(),
        name = name,
        description = description
    )
}
