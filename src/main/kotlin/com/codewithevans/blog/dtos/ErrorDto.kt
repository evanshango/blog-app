package com.codewithevans.blog.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import java.time.LocalDateTime

data class ErrorDto(
    var status: String? = null,
    var message: String? = null,
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var timestamp: LocalDateTime? = null
)
