package com.codewithevans.blog.controllers

import com.codewithevans.blog.dtos.ErrorDto
import com.codewithevans.blog.dtos.RoleDto
import com.codewithevans.blog.requests.RoleReq
import com.codewithevans.blog.services.RoleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid

@Tag(name = "Roles", description = "The Roles API Operations")
@RestController
@RequestMapping(value = ["/api/v1/roles"], produces = [MediaType.APPLICATION_JSON_VALUE])
class RoleController(private val roleService: RoleService) {

    @GetMapping
    @Operation(
        summary = "Endpoint for fetching a list of available roles",
        responses = [
            ApiResponse(
                responseCode = "200", description = "OK", content = [Content(
                    array = ArraySchema(schema = Schema(implementation = RoleDto::class))
                )]
            ),
            ApiResponse(
                responseCode = "500", description = "Internal server error", content = [Content()]
            )
        ]
    )
    fun fetchRoles(): ResponseEntity<Flux<RoleDto>> = ResponseEntity.ok(roleService.getRoles())

    @PostMapping
    @Operation(
        summary = "Endpoint for Creating a role",
        responses = [
            ApiResponse(
                responseCode = "201", description = "Role created successfully", content = [Content(
                    schema = Schema(implementation = RoleDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "409", description = "Role with provided name already exists", content = [Content(
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
    fun createRole(
        @Valid @RequestBody roleReq: RoleReq
    ): ResponseEntity<Mono<RoleDto>> = ResponseEntity.status(HttpStatus.CREATED).body(
        mono { roleService.createRole(roleReq) }
    )

    @GetMapping("/{roleId}")
    @Operation(
        summary = "Endpoint for fetching an existing role",
        responses = [
            ApiResponse(
                responseCode = "200", description = "OK", content = [Content(
                    schema = Schema(implementation = RoleDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "Role with provided roleId not found", content = [Content(
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
    fun fetchRoleById(
        @PathVariable(name = "roleId") roleId: UUID
    ): ResponseEntity<Mono<RoleDto>> = ResponseEntity.ok(mono { roleService.fetchRoleById(roleId) })

    @PutMapping("/{roleId}")
    @Operation(
        summary = "Endpoint for updating an existing role",
        responses = [
            ApiResponse(
                responseCode = "200", description = "Role updated successfully", content = [Content(
                    schema = Schema(implementation = RoleDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "Role with provided roleId not found", content = [Content(
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
    fun updateRole(
        @PathVariable(name = "roleId") roleId: UUID, @Valid @RequestBody roleReq: RoleReq
    ): ResponseEntity<Mono<RoleDto>> = ResponseEntity.ok(mono { roleService.updateRole(roleId, roleReq) })

    @DeleteMapping("/{roleId}")
    @Operation(
        summary = "Endpoint for deleting an existing role",
        responses = [
            ApiResponse(
                responseCode = "204", description = "Role deleted successfully", content = [Content()]
            ),
            ApiResponse(
                responseCode = "404", description = "Role with provided roleId not found", content = [Content(
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
    fun deleteRole(
        @PathVariable(name = "roleId") roleId: UUID
    ): ResponseEntity<Mono<*>> = ResponseEntity(mono { roleService.deleteRole(roleId) }, HttpStatus.NO_CONTENT)
}