package com.codewithevans.blog.requests

import javax.validation.constraints.NotBlank

data class PostReq(
    @NotBlank(message = "Post title is required")
    val title: String,
    @NotBlank(message = "Post content is required")
    val content: String
)
