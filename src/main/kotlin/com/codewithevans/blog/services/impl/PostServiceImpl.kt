package com.codewithevans.blog.services.impl

import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.dtos.PostDto
import com.codewithevans.blog.dtos.toPost
import com.codewithevans.blog.entities.Post
import com.codewithevans.blog.entities.toPostDto
import com.codewithevans.blog.exceptions.ResourceExists
import com.codewithevans.blog.exceptions.ResourceNotFound
import com.codewithevans.blog.repositories.PostRepository
import com.codewithevans.blog.requests.PostReq
import com.codewithevans.blog.services.PostService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class PostServiceImpl : PostService {

    @Autowired
    lateinit var postRepository: PostRepository

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
        return withContext(Dispatchers.IO) {
            postRepository.findById(postId).orElseThrow { ResourceNotFound("Post with id '$postId' not found") }
        }.toPostDto()
    }

    override suspend fun createPost(postReq: PostReq): PostDto {
        val existing = withContext(Dispatchers.IO) {
            postRepository.findByTitleIgnoreCase(postReq.title)
        }

        if (existing != null) throw ResourceExists("Post with title ${postReq.title} already exists")

        val newPost = Post(
            title = postReq.title,
            content = postReq.content,
            createdAt = LocalDateTime.now()
        )
        return withContext(Dispatchers.IO) { postRepository.save(newPost) }.toPostDto()
    }

    override suspend fun updatePost(postId: UUID, postReq: PostReq): PostDto? {
        val existing = fetchPostById(postId)?.toPost()

        return existing?.let {
            it.title = postReq.title
            it.content = postReq.content
            it.updatedAt = LocalDateTime.now()

            postRepository.save(it)

        }?.toPostDto()
    }

    override suspend fun deletePost(postId: UUID) {
        val existing = fetchPostById(postId)?.toPost()
        existing?.let {
            postRepository.delete(it)
        }
    }
}