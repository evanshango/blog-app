package com.codewithevans.blog.services.impl

import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.dtos.ReplyDto
import com.codewithevans.blog.entities.Reply
import com.codewithevans.blog.exceptions.NotPermitted
import com.codewithevans.blog.exceptions.ResourceNotFound
import com.codewithevans.blog.helpers.Utils
import com.codewithevans.blog.helpers.toReplyDto
import com.codewithevans.blog.repositories.CommentRepository
import com.codewithevans.blog.repositories.ReplyRepository
import com.codewithevans.blog.requests.ReplyReq
import com.codewithevans.blog.security.JwtService
import com.codewithevans.blog.services.ReplyService
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class ReplyServiceImpl(
    private val replyRepository: ReplyRepository, private val commentRepository: CommentRepository,
    private val utils: Utils, private val jwtService: JwtService
) : ReplyService {

    override fun fetchReplies(
        commentId: UUID, pageNo: Int?, pageSize: Int?, orderBy: String?, orderDir: String?
    ): PaginationDto<ReplyDto> {
        val page = if (pageNo!! > 0) pageNo - 1 else 0
        val sort = if (orderDir?.equals(Sort.Direction.ASC.name, true) == true)
            Sort.by(orderBy).ascending() else Sort.by(orderBy).descending()

        val existingComment = commentRepository.findById(commentId).orElseThrow {
            ResourceNotFound("Comment with id $commentId")
        }

        val replyContent = replyRepository.findAllByComment(existingComment, PageRequest.of(page, pageSize!!, sort))

        return PaginationDto(
            pageNo = replyContent.number + 1,
            pageSize = replyContent.size,
            totalPages = replyContent.totalPages,
            totalItems = replyContent.totalElements,
            content = replyContent.content.map {it.toReplyDto()}
        )
    }

    override suspend fun createReply(commentId: UUID, replyReq: ReplyReq): ReplyDto = withContext(IO) {
        val user = utils.getUser(jwtService.getUsernameOrEmail()) ?: throw ResourceNotFound("User not found")

        val existingComment = commentRepository.findById(commentId).orElseThrow {
            ResourceNotFound("Comment with id '$commentId' not found")
        }
        val newReply = Reply(reply = replyReq.reply, comment = existingComment, user = user)

        return@withContext replyRepository.save(newReply).toReplyDto()
    }

    override suspend fun updateReply(replyId: UUID, replyReq: ReplyReq): ReplyDto? = withContext(IO) {
        val existing = checkReplyAndUser(replyId, "update")

        existing.reply = replyReq.reply
        existing.updatedAt = LocalDateTime.now()

        return@withContext replyRepository.save(existing).toReplyDto()
    }

    override suspend fun deleteReply(replyId: UUID) = withContext(IO) {
        val existing = checkReplyAndUser(replyId, "delete")
        return@withContext replyRepository.delete(existing)
    }

    private suspend fun checkReplyAndUser(replyId: UUID, action: String) = withContext(IO) {
        return@withContext replyRepository.findById(replyId).orElseThrow {
            ResourceNotFound("Reply with id '$replyId' not found")
        }.also {
            val user = utils.getUser(jwtService.getUsernameOrEmail()) ?: throw ResourceNotFound("User not found")
            if (it.user!! != user) throw NotPermitted("Unable to $action reply")
        }
    }
}