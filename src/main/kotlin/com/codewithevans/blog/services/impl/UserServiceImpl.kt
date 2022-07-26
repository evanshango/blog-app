package com.codewithevans.blog.services.impl

import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.dtos.UserDto
import com.codewithevans.blog.entities.User
import com.codewithevans.blog.exceptions.NotPermitted
import com.codewithevans.blog.exceptions.ResourceNotFound
import com.codewithevans.blog.helpers.Constants.comment_count
import com.codewithevans.blog.helpers.Constants.getPageable
import com.codewithevans.blog.helpers.Constants.post_count
import com.codewithevans.blog.helpers.Constants.reply_count
import com.codewithevans.blog.helpers.Utils
import com.codewithevans.blog.helpers.toUserDto
import com.codewithevans.blog.repositories.CommentRepository
import com.codewithevans.blog.repositories.PostRepository
import com.codewithevans.blog.repositories.ReplyRepository
import com.codewithevans.blog.repositories.UserRepository
import com.codewithevans.blog.requests.UserReq
import com.codewithevans.blog.security.JwtService
import com.codewithevans.blog.services.UserService
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository, private val postRepository: PostRepository,
    private val commentRepository: CommentRepository, private val replyRepository: ReplyRepository,
    private val utils: Utils, private val jwtService: JwtService
) : UserService {
    override suspend fun fetchUsers(
        pageNo: Int?, pageSize: Int?, orderBy: String?, orderDir: String?, search: String?
    ): PaginationDto<UserDto> {
        val pageable = getPageable(pageNo = pageNo, pageSize = pageSize, orderDir = orderDir, orderBy = orderBy)

        val userContent = if (search != null)
            withContext(IO) {
                userRepository.findAllByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCaseOrEmailContainsIgnoreCase(
                    search, search, search, pageable
                )
            }
        else userRepository.findAll(pageable)

        return PaginationDto(
            pageNo = userContent.number + 1,
            pageSize = userContent.size,
            totalPages = userContent.totalPages,
            totalItems = userContent.totalElements,
            content = userContent.content.map {
                val countMap = fetchUserPostCommentAndReplyCount(it)
                val postCount = countMap[post_count]
                val commentCount = countMap[comment_count]
                val replyCount = countMap[reply_count]
                it.toUserDto(postCount = postCount!!, commentCount = commentCount!!, replyCount = replyCount!!)
            }
        )
    }

    override suspend fun fetchUserById(userId: UUID): UserDto? = withContext(IO) {
        val postCount: Int?
        val commentCount: Int?
        val replyCount: Int?
        return@withContext userRepository.findById(userId).orElseThrow {
            ResourceNotFound("User with userId $userId not found")
        }.also {
            val countMap = fetchUserPostCommentAndReplyCount(it)
            postCount = countMap[post_count]
            commentCount = countMap[comment_count]
            replyCount = countMap[reply_count]
        }.toUserDto(postCount = postCount!!, commentCount = commentCount!!, replyCount = replyCount!!)
    }

    override suspend fun updateUser(userId: UUID, userReq: UserReq): UserDto? = withContext(IO) {
        val user = utils.getUser(jwtService.getUsernameOrEmail()) ?: throw ResourceNotFound("User not found")
        if (user.id != userId) throw NotPermitted("Unable to update user")

        user.firstName = userReq.firstName ?: user.firstName
        user.lastName = userReq.lastName ?: user.lastName
        user.email = userReq.email ?: user.email
        user.updatedAt = LocalDateTime.now()

        var countMap: MutableMap<String, Int>

        val update = userRepository.save(user).also {
            countMap = fetchUserPostCommentAndReplyCount(it)
        }

        return@withContext update.toUserDto(
            postCount = countMap[post_count]!!,
            commentCount = countMap[comment_count]!!,
            replyCount = countMap[reply_count]!!
        )
    }

    override suspend fun deleteUser(userId: UUID) = withContext(IO) {
        val existing = userRepository.findById(userId).orElseThrow {
            ResourceNotFound("User with userId $userId not found")
        }

        replyRepository.findAllByUser(existing).also { replies ->
            if (replies.isNotEmpty()) replies.forEach { replyRepository.delete(it) }
        }

        commentRepository.findAllByUser(existing).also { comments ->
            if (comments.isNotEmpty()) comments.forEach { commentRepository.delete(it) }
        }

        postRepository.findAllByUser(existing).also { posts ->
            if (posts.isNotEmpty()) posts.forEach { postRepository.delete(it) }
        }

        return@withContext userRepository.delete(existing)
    }

    private fun fetchUserPostCommentAndReplyCount(user: User): MutableMap<String, Int> {
        val map: MutableMap<String, Int> = HashMap()

        val postCount = postRepository.countAllByUser(user)
        val commentCount = commentRepository.countAllByUser(user)
        val replyCount = replyRepository.countAllByUser(user)

        map[post_count] = postCount
        map[comment_count] = commentCount
        map[reply_count] = replyCount

        return map
    }
}