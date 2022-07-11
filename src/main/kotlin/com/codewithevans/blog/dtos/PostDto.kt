package com.codewithevans.blog.dtos

import com.codewithevans.blog.entities.Post
import com.codewithevans.blog.entities.User
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PostDto(
    val id: String,
    val title: String,
    val slug: String,
    val content: String,
    val image: String? = null,
    val author: AuthorDto? = null,
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val updatedAt: LocalDateTime? = null
)

fun PostDto.toPost(): Post {
    return Post(
        id = UUID.fromString(id),
        title = title,
        slug = slug,
        content = content,
        user = User(
            id = UUID.fromString(author?.id),
            firstName = author!!.firstName,
            lastName = author.lastName,
            email = author.email
        ),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
