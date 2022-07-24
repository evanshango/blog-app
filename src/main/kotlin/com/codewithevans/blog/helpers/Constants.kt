package com.codewithevans.blog.helpers

import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

object Constants {
    const val user_role = "ROLE_USER"
    const val admin_role = "ROLE_ADMIN"

    fun getResponseBody(message: String, status: HttpStatus): MutableMap<String, Any> {
        val data: MutableMap<String, Any> = HashMap()
        data["status"] = status.toString()
        data["message"] = message
        data["timestamp"] = LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss")).toString()
        return data
    }
}