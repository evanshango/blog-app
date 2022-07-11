package com.codewithevans.blog.services

import com.codewithevans.blog.dtos.CommentDto
import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.requests.CommentReq
import java.util.*

interface CommentService {

    fun fetchComments(
        postId: UUID, pageNo: Int?, pageSize: Int?, orderBy: String?, orderDir: String?
    ): PaginationDto<CommentDto>

    suspend fun fetchCommentById(commentId: UUID): CommentDto?

    suspend fun createComment(postId: UUID, commentReq: CommentReq): CommentDto

    suspend fun updateComment(commentId: UUID, commentReq: CommentReq): CommentDto?

    suspend fun deleteComment(commentId: UUID)
}