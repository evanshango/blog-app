package com.codewithevans.blog.services

import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.dtos.ReplyDto
import com.codewithevans.blog.requests.ReplyReq
import java.util.*

interface ReplyService {

    fun fetchReplies(
        commentId: UUID, pageNo: Int?, pageSize: Int?, orderBy: String?, orderDir: String?
    ): PaginationDto<ReplyDto>

    suspend fun createReply(commentId: UUID, replyReq: ReplyReq): ReplyDto

    suspend fun updateReply(replyId: UUID, replyReq: ReplyReq): ReplyDto?

    suspend fun deleteReply(replyId: UUID)
}