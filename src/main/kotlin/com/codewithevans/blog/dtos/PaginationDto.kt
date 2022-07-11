package com.codewithevans.blog.dtos

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PaginationDto<T>(
    val pageNo: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalItems: Long,
    val content: List<T>
)