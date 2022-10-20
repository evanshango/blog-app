package com.codewithevans.blog.responses

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class PassResponse(
    val status: String,
    val message: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val timeStamp: LocalDateTime
)
