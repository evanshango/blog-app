package com.codewithevans.blog.controllers

import com.codewithevans.blog.dtos.CommentDto
import com.codewithevans.blog.dtos.ErrorDto
import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.requests.CommentReq
import com.codewithevans.blog.services.CommentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid

@Tag(name = "Comments", description = "The Comments API Operations")
@RestController
@RequestMapping(value = ["/api/v1"], produces = [MediaType.APPLICATION_JSON_VALUE])
class CommentController(private val commentService: CommentService) {

    @GetMapping("/{postId}/comments")
    @Operation(
        summary = "Endpoint for fetching a list of available comments for a specific post",
        responses = [
            ApiResponse(
                responseCode = "200", description = "OK", content = [Content(
                    array = ArraySchema(schema = Schema(implementation = CommentDto::class))
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "Post with provided postId not found", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "500", description = "Internal server error", content = [Content()]
            )
        ]
    )
    fun fetchComments(
        @PathVariable(name = "postId") postId: UUID,
        @RequestParam(name = "pageNo", required = false, defaultValue = "1") pageNo: Int?,
        @RequestParam(name = "pageSize", required = false, defaultValue = "20") pageSize: Int?,
        @RequestParam(name = "orderBy", required = false, defaultValue = "createdAt") orderBy: String?,
        @RequestParam(name = "orderDir", required = false, defaultValue = "desc") orderDir: String?
    ): ResponseEntity<PaginationDto<CommentDto>> = ResponseEntity.ok(
        commentService.fetchComments(postId, pageNo, pageSize, orderBy, orderDir)
    )

    @PostMapping("/{postId}/comments")
    @Operation(
        summary = "Endpoint for adding a comment to an existing post",
        responses = [
            ApiResponse(
                responseCode = "201", description = "Comment created successfully", content = [Content(
                    schema = Schema(implementation = CommentDto::class)
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
    fun createComment(
        @PathVariable(name = "postId") postId: UUID, @RequestBody commentReq: CommentReq
    ): ResponseEntity<Mono<CommentDto>> = ResponseEntity.status(CREATED).body(
        mono { commentService.createComment(postId, commentReq) }
    )

    @GetMapping("/comments/{commentId}")
    @Operation(
        summary = "Endpoint for fetching an existing comment",
        responses = [
            ApiResponse(
                responseCode = "200", description = "OK", content = [Content(
                    schema = Schema(implementation = CommentDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "Comment with provided commentId not found", content = [Content(
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
    fun fetchCommentById(
        @PathVariable(name = "commentId") commentId: UUID
    ): ResponseEntity<Mono<CommentDto>> = ResponseEntity.ok(mono { commentService.fetchCommentById(commentId) })

    @PutMapping("/comments/{commentId}")
    @Operation(
        summary = "Endpoint for updating an existing comment",
        responses = [
            ApiResponse(
                responseCode = "200", description = "Comment updated successfully", content = [Content(
                    schema = Schema(implementation = CommentDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "Comment with provided commentId not found", content = [Content(
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
    fun updateComment(
        @PathVariable(name = "commentId") commentId: UUID, @Valid @RequestBody commentReq: CommentReq
    ): ResponseEntity<Mono<CommentDto>> = ResponseEntity.ok(
        mono { commentService.updateComment(commentId, commentReq) }
    )

    @DeleteMapping("/comments/{commentId}")
    @Operation(
        summary = "Endpoint for deleting an existing comment",
        responses = [
            ApiResponse(
                responseCode = "204", description = "Comment deleted successfully", content = [Content()]
            ),
            ApiResponse(
                responseCode = "404", description = "Comment with provided commentId not found", content = [Content(
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
    fun deleteComment(
        @PathVariable(name = "commentId") commentId: UUID
    ): ResponseEntity<Mono<*>> = ResponseEntity(mono { commentService.deleteComment(commentId) }, HttpStatus.NO_CONTENT)
}