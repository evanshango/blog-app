package com.codewithevans.blog.responses

data class AuthResponse(
    val name: String,
    val email: String,
    val token: String? = null
)
