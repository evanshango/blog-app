package com.codewithevans.blog.dtos

import com.codewithevans.blog.entities.BlogUser
import com.codewithevans.blog.entities.Comment
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CommentDto(
    val id: String,
    var comment: String,
    var author: AuthorDto? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val updatedAt: LocalDateTime? = null
)

fun CommentDto.toComment(): Comment {
    return Comment(
        id = UUID.fromString(id),
        comment = comment,
        user = BlogUser(
            id = UUID.fromString(author?.id),
            firstName = author!!.firstName,
            lastName = author!!.lastName,
            email = author!!.email
        ),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
