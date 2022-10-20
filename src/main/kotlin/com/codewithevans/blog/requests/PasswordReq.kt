package com.codewithevans.blog.requests

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class PasswordReq(
    @field:Email(regexp = ".+@.+\\..+", message = "Email should match the patter xyz@xyz.com")
    val email: String,
    val oldPassword: String,
    @field:NotBlank(message = "Password is required")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{6,}\$",
        message = "Invalid password"
    )
    val newPassword: String
)