package com.codewithevans.blog.dtos

import com.codewithevans.blog.entities.Role
import java.util.*

data class RoleDto(
    val id: String,
    val name: String,
    val description: String
)

fun RoleDto.toRole(): Role = Role(
    id = UUID.fromString(id),
    name = name,
    description = description
)