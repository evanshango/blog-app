package com.codewithevans.blog.services.impl

import com.codewithevans.blog.dtos.CommentDto
import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.entities.Comment
import com.codewithevans.blog.exceptions.NotPermitted
import com.codewithevans.blog.exceptions.ResourceNotFound
import com.codewithevans.blog.helpers.Utils
import com.codewithevans.blog.helpers.toCommentDto
import com.codewithevans.blog.repositories.CommentRepository
import com.codewithevans.blog.repositories.PostRepository
import com.codewithevans.blog.repositories.ReplyRepository
import com.codewithevans.blog.requests.CommentReq
import com.codewithevans.blog.security.JwtService
import com.codewithevans.blog.services.CommentService
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository, private val postRepository: PostRepository,
    private val replyRepository: ReplyRepository, private val jwtService: JwtService, private val utils: Utils
) : CommentService {
    override fun fetchComments(
        postId: UUID, pageNo: Int?, pageSize: Int?, orderBy: String?, orderDir: String?
    ): PaginationDto<CommentDto> {
        val page = if (pageNo!! > 0) pageNo - 1 else 0
        val sort = if (orderDir?.equals(Sort.Direction.ASC.name, true) == true)
            Sort.by(orderBy).ascending() else Sort.by(orderBy).descending()

        val existingPost = postRepository.findById(postId).orElseThrow { ResourceNotFound("Post with id $postId") }

        val commentContent = commentRepository.findAllByPost(existingPost, PageRequest.of(page, pageSize!!, sort))

        return PaginationDto(
            pageNo = commentContent.number + 1,
            pageSize = commentContent.size,
            totalPages = commentContent.totalPages,
            totalItems = commentContent.totalElements,
            content = commentContent.content.map {
                val repliesCount = replyRepository.countAllByComment(it)
                it.toCommentDto(repliesCount)
            }
        )
    }

    override suspend fun fetchCommentById(commentId: UUID): CommentDto? = withContext(IO) {
        val repliesCount: Int
        return@withContext commentRepository.findById(commentId).orElseThrow {
            ResourceNotFound("Comment with id '$commentId' not found")
        }.also {
            repliesCount = replyRepository.countAllByComment(it)
        }.toCommentDto(repliesCount)
    }

    override suspend fun createComment(postId: UUID, commentReq: CommentReq): CommentDto = withContext(IO) {
        val user = utils.getUser(jwtService.getUsernameOrEmail()) ?: throw ResourceNotFound("User not found")

        val existingPost = postRepository.findById(postId).orElseThrow { ResourceNotFound("Post with id $postId") }

        val newComment = Comment(comment = commentReq.comment, post = existingPost, user = user)

        return@withContext commentRepository.save(newComment).toCommentDto(0)
    }

    override suspend fun updateComment(commentId: UUID, commentReq: CommentReq): CommentDto? = withContext(IO) {
        val repliesCount: Int
        val existing = checkCommentAndUser(commentId, "update")

        existing.comment = commentReq.comment
        existing.updatedAt = LocalDateTime.now()

        return@withContext commentRepository.save(existing).also {
            repliesCount = replyRepository.countAllByComment(it)
        }.toCommentDto(repliesCount)
    }

    override suspend fun deleteComment(commentId: UUID) = withContext(IO) {
        val existing = checkCommentAndUser(commentId, "delete")

        replyRepository.findAllByComment(existing).also { replies ->
            if (replies.isNotEmpty()) {
                replies.forEach { replyRepository.delete(it) }
            }
        }

        return@withContext commentRepository.delete(existing)
    }

    private suspend fun checkCommentAndUser(commentId: UUID, action: String) = withContext(IO) {
        return@withContext commentRepository.findById(commentId).orElseThrow {
            ResourceNotFound("Comment with id '$commentId' not found")
        }.also {
            val user = utils.getUser(jwtService.getUsernameOrEmail()) ?: throw ResourceNotFound("User not found")
            if (it.user!! != user) throw NotPermitted("Unable to $action comment")
        }
    }
}