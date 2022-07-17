package com.codewithevans.blog.requests

data class SignupReq(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)
