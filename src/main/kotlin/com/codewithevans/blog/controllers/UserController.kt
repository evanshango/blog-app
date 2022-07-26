package com.codewithevans.blog.controllers

import com.codewithevans.blog.dtos.ErrorDto
import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.dtos.PostDto
import com.codewithevans.blog.dtos.UserDto
import com.codewithevans.blog.requests.UserReq
import com.codewithevans.blog.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid

@Tag(name = "Users", description = "The Users API Operations")
@RestController
@RequestMapping(value = ["/api/v1/users"], produces = [MediaType.APPLICATION_JSON_VALUE])
class UserController(private val userService: UserService) {

    @GetMapping
    @Operation(
        summary = "Endpoint for fetching a list of available users",
        responses = [
            ApiResponse(
                responseCode = "200", description = "OK", content = [Content(
                    array = ArraySchema(schema = Schema(implementation = UserDto::class))
                )]
            ),
            ApiResponse(
                responseCode = "500", description = "Internal server error", content = [Content()]
            )
        ]
    )
    fun fetchUsers(
        @RequestParam(name = "pageNo", required = false, defaultValue = "1") pageNo: Int?,
        @RequestParam(name = "pageSize", required = false, defaultValue = "20") pageSize: Int?,
        @RequestParam(name = "orderBy", required = false, defaultValue = "createdAt") orderBy: String?,
        @RequestParam(name = "orderDir", required = false, defaultValue = "desc") orderDir: String?,
        @RequestParam(name = "search", required = false) search: String?
    ): Mono<ResponseEntity<PaginationDto<UserDto>>> = mono {
        ResponseEntity.ok(userService.fetchUsers(pageNo, pageSize, orderBy, orderDir, search))
    }

    @GetMapping("/{userId}")
    @Operation(
        summary = "Endpoint for fetching an existing user",
        responses = [
            ApiResponse(
                responseCode = "200", description = "OK", content = [Content(
                    schema = Schema(implementation = UserDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "User with provided userId not found", content = [Content(
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
    fun fetchUserById(
        @PathVariable(name = "userId") userId: UUID
    ): Mono<ResponseEntity<UserDto>> = mono { ResponseEntity.ok(userService.fetchUserById(userId)) }

    @PutMapping("/{userId}")
    @Operation(
        summary = "Endpoint for updating an existing user",
        responses = [
            ApiResponse(
                responseCode = "200", description = "User updated successfully", content = [Content(
                    schema = Schema(implementation = PostDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "403", description = "Forbidden", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "User with provided userId not found", content = [Content(
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
    fun updateUser(
        @PathVariable(name = "userId") userId: UUID, @Valid @RequestBody userReq: UserReq
    ): Mono<ResponseEntity<UserDto>> = mono { ResponseEntity.ok(userService.updateUser(userId, userReq)) }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Endpoint for deleting an existing user",
        responses = [
            ApiResponse(
                responseCode = "204", description = "User deleted successfully", content = [Content()]
            ),
            ApiResponse(
                responseCode = "403", description = "Forbidden", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "User with provided userId not found", content = [Content(
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
    fun deleteUser(
        @PathVariable(name = "userId") userId: UUID
    ): Mono<ResponseEntity<*>> = mono { ResponseEntity(userService.deleteUser(userId), HttpStatus.NO_CONTENT) }
}