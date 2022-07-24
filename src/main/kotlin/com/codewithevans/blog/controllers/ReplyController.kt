package com.codewithevans.blog.controllers

import com.codewithevans.blog.dtos.ErrorDto
import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.dtos.ReplyDto
import com.codewithevans.blog.requests.ReplyReq
import com.codewithevans.blog.services.ReplyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
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

@Tag(name = "Replies", description = "The Replies API Operations")
@RestController
@RequestMapping(value = ["/api/v1"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ReplyController(private val replyService: ReplyService) {

    @GetMapping("/{commentId}/replies")
    @Operation(
        summary = "Endpoint for fetching a list of available replies for a specific comment",
        responses = [
            ApiResponse(
                responseCode = "200", description = "OK", content = [Content(
                    array = ArraySchema(schema = Schema(implementation = ReplyDto::class))
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "Comment with provided commentId not found", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "500", description = "Internal server error", content = [Content()]
            )
        ]
    )
    fun fetchReplies(
        @PathVariable(name = "commentId") commentId: UUID,
        @RequestParam(name = "pageNo", required = false, defaultValue = "1") pageNo: Int?,
        @RequestParam(name = "pageSize", required = false, defaultValue = "20") pageSize: Int?,
        @RequestParam(name = "orderBy", required = false, defaultValue = "createdAt") orderBy: String?,
        @RequestParam(name = "orderDir", required = false, defaultValue = "desc") orderDir: String?
    ): Mono<ResponseEntity<PaginationDto<ReplyDto>>> = mono {
        ResponseEntity.ok(
            replyService.fetchReplies(commentId, pageNo, pageSize, orderBy, orderDir)
        )
    }

    @PostMapping("/{commentId}/replies")
    @Operation(
        summary = "Endpoint for adding a reply to an existing comment",
        responses = [
            ApiResponse(
                responseCode = "201", description = "Reply created successfully", content = [Content(
                    schema = Schema(implementation = ReplyDto::class)
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
        ], security = [SecurityRequirement(name = "Jwt")]
    )
    fun createReply(
        @PathVariable(name = "commentId") commentId: UUID, @RequestBody replyReq: ReplyReq
    ): Mono<ResponseEntity<ReplyDto>> = mono {
        ResponseEntity.status(CREATED).body(replyService.createReply(commentId, replyReq))
    }

    @PutMapping("/replies/{replyId}")
    @Operation(
        summary = "Endpoint for updating an existing reply",
        responses = [
            ApiResponse(
                responseCode = "200", description = "Reply updated successfully", content = [Content(
                    schema = Schema(implementation = ReplyDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "Reply with provided replyId not found", content = [Content(
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
    fun updateReply(
        @PathVariable(name = "replyId") replyId: UUID, @Valid @RequestBody replyReq: ReplyReq
    ): Mono<ResponseEntity<ReplyDto>> = mono { ResponseEntity.ok(replyService.updateReply(replyId, replyReq)) }

    @DeleteMapping("/replies/{replyId}")
    @Operation(
        summary = "Endpoint for deleting an existing reply",
        responses = [
            ApiResponse(
                responseCode = "204", description = "Reply deleted successfully", content = [Content()]
            ),
            ApiResponse(
                responseCode = "404", description = "Reply with provided replyId not found", content = [Content(
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
    fun deleteReply(
        @PathVariable(name = "replyId") replyId: UUID
    ): Mono<ResponseEntity<*>> = mono { ResponseEntity(replyService.deleteReply(replyId), HttpStatus.NO_CONTENT) }
}