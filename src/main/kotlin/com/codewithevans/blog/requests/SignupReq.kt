package com.codewithevans.blog.requests

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class SignupReq(
    @field:NotBlank(message = "FirstName is required")
    val firstName: String,
    @field:NotBlank(message = "LastName address is required")
    val lastName: String,
    @field:NotBlank(message = "Email address is required")
    @field:Email(regexp = ".+@.+\\..+", message = "Email should match the patter xyz@xyz.com")
    val email: String,
    @field:NotBlank(message = "Password is required")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{6,}\$",
        message = "Invalid password"
    )
    val password: String
)