package com.codewithevans.blog.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorDto(
    var status: String? = null,
    var message: String? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var timestamp: LocalDateTime? = null
)
