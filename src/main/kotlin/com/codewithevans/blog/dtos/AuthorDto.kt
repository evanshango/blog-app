package com.codewithevans.blog.dtos

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthorDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val image: String? = null
)