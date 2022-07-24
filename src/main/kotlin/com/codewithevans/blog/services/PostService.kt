package com.codewithevans.blog.services

import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.dtos.PostDto
import com.codewithevans.blog.requests.PostReq
import java.util.*

interface PostService {

    suspend fun fetchPosts(pageNo: Int?, pageSize: Int?, orderBy: String?, orderDir: String?): PaginationDto<PostDto>

    suspend fun fetchPostById(postId: UUID): PostDto?

    suspend fun createPost(postReq: PostReq): PostDto

    suspend fun updatePost(postId: UUID, postReq: PostReq): PostDto?

    suspend fun deletePost(postId: UUID)
}