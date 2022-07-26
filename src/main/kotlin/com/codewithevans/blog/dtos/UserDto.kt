package com.codewithevans.blog.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import java.util.UUID

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto(
    var id: UUID,
    var firstName: String,
    var lastName: String,
    var email: String,
    var roles: List<RoleDto>?,
    var posts: Int? = null,
    var comments: Int? = null,
    var replies: Int? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var createdAt: LocalDateTime? = null
)