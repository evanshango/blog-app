package com.codewithevans.blog.services.impl

import com.codewithevans.blog.dtos.CommentDto
import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.requests.CommentReq
import com.codewithevans.blog.services.CommentService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommentServiceImpl : CommentService {
    override fun fetchComments(
        postId: UUID, pageNo: Int?, pageSize: Int?, orderBy: String?, orderDir: String?
    ): PaginationDto<CommentDto> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCommentById(commentId: UUID): CommentDto? {
        TODO("Not yet implemented")
    }

    override suspend fun createComment(postId: UUID, commentReq: CommentReq): CommentDto {
        TODO("Not yet implemented")
    }

    override suspend fun updateComment(commentId: UUID, commentReq: CommentReq): CommentDto? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteComment(commentId: UUID) {
        TODO("Not yet implemented")
    }
}