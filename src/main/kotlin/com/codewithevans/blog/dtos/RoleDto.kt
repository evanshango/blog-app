package com.codewithevans.blog.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class RoleDto(
    val id: String,
    val name: String,
    val description: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime
)