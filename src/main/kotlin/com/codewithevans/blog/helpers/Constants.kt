package com.codewithevans.blog.helpers

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

object Constants {
    const val user_role = "ROLE_USER"
    const val admin_role = "ROLE_ADMIN"
    const val post_count = "postCount"
    const val comment_count = "commentCount"
    const val reply_count = "replyCount"

    fun getResponseBody(message: String, status: HttpStatus): MutableMap<String, Any> {
        val data: MutableMap<String, Any> = HashMap()
        data["status"] = status.toString()
        data["message"] = message
        data["timestamp"] = LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss")).toString()
        return data
    }

    fun getPageable(pageNo: Int?, pageSize: Int?, orderDir: String?, orderBy: String?): Pageable {
        val page = if (pageNo!! > 0) pageNo - 1 else 0
        val sort = if (orderDir?.equals(Sort.Direction.ASC.name, true) == true)
            Sort.by(orderBy).ascending() else Sort.by(orderBy).descending()
        return PageRequest.of(page, pageSize!!, sort)
    }
}