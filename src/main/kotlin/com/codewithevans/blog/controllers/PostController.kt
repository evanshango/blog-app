package com.codewithevans.blog.controllers

import com.codewithevans.blog.dtos.ErrorDto
import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.dtos.PostDto
import com.codewithevans.blog.requests.PostReq
import com.codewithevans.blog.services.PostService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid

@Tag(name = "Posts", description = "The Posts API Operations")
@RestController
@RequestMapping(value = ["/api/v1/posts"], produces = [MediaType.APPLICATION_JSON_VALUE])
class PostController(private val postService: PostService) {

    @GetMapping
    @Operation(
        summary = "Endpoint for fetching a list of available posts",
        responses = [
            ApiResponse(
                responseCode = "200", description = "OK", content = [Content(
                    array = ArraySchema(schema = Schema(implementation = PostDto::class))
                )]
            ),
            ApiResponse(
                responseCode = "500", description = "Internal server error", content = [Content()]
            )
        ]
    )
    fun fetchPosts(
        @RequestParam(name = "pageNo", required = false, defaultValue = "1") pageNo: Int?,
        @RequestParam(name = "pageSize", required = false, defaultValue = "20") pageSize: Int?,
        @RequestParam(name = "orderBy", required = false, defaultValue = "createdAt") orderBy: String?,
        @RequestParam(name = "orderDir", required = false, defaultValue = "desc") orderDir: String?
    ): Mono<ResponseEntity<PaginationDto<PostDto>>> = mono {
        ResponseEntity.ok(postService.fetchPosts(pageNo, pageSize, orderBy, orderDir))
    }

    @PostMapping
    @Operation(
        summary = "Endpoint for Creating a post",
        responses = [
            ApiResponse(
                responseCode = "201", description = "Post created successfully", content = [Content(
                    schema = Schema(implementation = PostDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "409", description = "Post with provided title already exists", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "500", description = "Internal server error", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            )
        ], security = [SecurityRequirement(name = "Jwt")]
    )
    fun createPost(
        @Valid @RequestBody postReq: PostReq
    ): Mono<ResponseEntity<PostDto>> = mono { ResponseEntity.status(CREATED).body(postService.createPost(postReq)) }

    @GetMapping("/{postId}")
    @Operation(
        summary = "Endpoint for fetching an existing post",
        responses = [
            ApiResponse(
                responseCode = "200", description = "OK", content = [Content(
                    schema = Schema(implementation = PostDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "Post with provided postId not found", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "500", description = "Internal server error", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            )
        ]
    )
    fun fetchPostById(
        @PathVariable(name = "postId") postId: UUID
    ): Mono<ResponseEntity<PostDto>> = mono { ResponseEntity.ok(postService.fetchPostById(postId)) }

    @PutMapping("/{postId}")
    @Operation(
        summary = "Endpoint for updating an existing post",
        responses = [
            ApiResponse(
                responseCode = "200", description = "Post updated successfully", content = [Content(
                    schema = Schema(implementation = PostDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "403", description = "Forbidden", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "Post with provided postId not found", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "500", description = "Internal server error", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            )
        ], security = [SecurityRequirement(name = "Jwt")]
    )
    fun updatePost(
        @PathVariable(name = "postId") postId: UUID, @Valid @RequestBody postReq: PostReq
    ): Mono<ResponseEntity<PostDto>> = mono { ResponseEntity.ok(postService.updatePost(postId, postReq)) }

    @DeleteMapping("/{postId}")
    @Operation(
        summary = "Endpoint for deleting an existing post",
        responses = [
            ApiResponse(
                responseCode = "204", description = "Post deleted successfully", content = [Content()]
            ),
            ApiResponse(
                responseCode = "403", description = "Forbidden", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "Post with provided postId not found", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "500", description = "Internal server error", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            )
        ], security = [SecurityRequirement(name = "Jwt")]
    )
    fun deletePost(
        @PathVariable(name = "postId") postId: UUID
    ): Mono<ResponseEntity<*>> = mono { ResponseEntity(postService.deletePost(postId), NO_CONTENT) }
}