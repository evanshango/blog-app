package com.codewithevans.blog.services.impl

import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.dtos.PostDto
import com.codewithevans.blog.entities.Post
import com.codewithevans.blog.exceptions.ResourceExists
import com.codewithevans.blog.exceptions.ResourceNotFound
import com.codewithevans.blog.helpers.Utils
import com.codewithevans.blog.helpers.toPostDto
import com.codewithevans.blog.repositories.PostRepository
import com.codewithevans.blog.requests.PostReq
import com.codewithevans.blog.security.JwtService
import com.codewithevans.blog.services.PostService
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class PostServiceImpl(private val jwtService: JwtService, private val utils: Utils) : PostService {
    @Autowired
    lateinit var postRepository: PostRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun fetchPosts(pageNo: Int?, pageSize: Int?, orderBy: String?, orderDir: String?): PaginationDto<PostDto> {
        val page = if (pageNo!! > 0) pageNo - 1 else 0
        val sort = if (orderDir?.equals(Sort.Direction.ASC.name, true) == true)
            Sort.by(orderBy).ascending() else Sort.by(orderBy).descending()

        val postContent = postRepository.findAll(PageRequest.of(page, pageSize!!, sort))

        return PaginationDto(
            pageNo = postContent.number + 1,
            pageSize = postContent.size,
            totalPages = postContent.totalPages,
            totalItems = postContent.totalElements,
            content = postContent.content.map { it.toPostDto() }
        )
    }

    override suspend fun fetchPostById(postId: UUID): PostDto? {
        return withContext(IO) {
            postRepository.findById(postId).orElseThrow { ResourceNotFound("Post with id '$postId' not found") }
        }.toPostDto()
    }

    override suspend fun createPost(postReq: PostReq): PostDto {
        val user = utils.getUser(jwtService.getUsernameOrEmail()) ?: throw ResourceNotFound("User not found")

        val existing = withContext(IO) {
            postRepository.findByTitleIgnoreCase(postReq.title)
        }

        if (existing != null) throw ResourceExists("Post with title ${postReq.title} already exists")

        val newPost = Post(
            title = postReq.title,
            content = postReq.content,
            createdAt = LocalDateTime.now(),
            user = user
        )
        return withContext(IO) { postRepository.save(newPost) }.toPostDto()
    }

    override suspend fun updatePost(postId: UUID, postReq: PostReq): PostDto? = withContext(IO) {
        val existing = postRepository.findById(postId).orElseThrow {
            ResourceNotFound("Post with id '$postId' not found")
        }

        existing.title = postReq.title
        existing.content = postReq.content
        existing.updatedAt = LocalDateTime.now()

        logger.info("Updating post with id $postId")

        return@withContext postRepository.save(existing).toPostDto()
    }

    override suspend fun deletePost(postId: UUID) = withContext(IO) {
        val existing = postRepository.findById(postId).orElseThrow {
            ResourceNotFound("Post with id '$postId' not found")
        }
        return@withContext postRepository.delete(existing)
    }
}