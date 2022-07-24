package com.codewithevans.blog.controllers

import com.codewithevans.blog.dtos.ErrorDto
import com.codewithevans.blog.requests.SigninReq
import com.codewithevans.blog.requests.SignupReq
import com.codewithevans.blog.responses.AuthResponse
import com.codewithevans.blog.services.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@Tag(name = "Authentication", description = "The Authentication API Operations")
@RestController
@RequestMapping(value = ["/api/v1/auth"], produces = [MediaType.APPLICATION_JSON_VALUE])
class AuthController(private val authService: AuthService) {

    @PostMapping("/signup")
    @Operation(
        summary = "Endpoint for Creating a user account",
        responses = [
            ApiResponse(
                responseCode = "201", description = "User created successfully", content = [Content(
                    schema = Schema(implementation = AuthResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400", description = "Bad Request", content = [Content(
                    schema = Schema(implementation = ErrorDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "409", description = "User already exists", content = [Content(
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
    fun signupUser(
        @RequestBody @Validated signupReq: SignupReq
    ): Mono<ResponseEntity<AuthResponse>> = mono {
        ResponseEntity.status(CREATED).body(authService.signupUser(signupReq))
    }

    @PostMapping("/signin")
    @Operation(
        summary = "Endpoint for Authenticating an existing user",
        responses = [
            ApiResponse(
                responseCode = "200", description = "User authenticated", content = [Content(
                    schema = Schema(implementation = AuthResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "401", description = "Invalid credentials", content = [Content(
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
    fun signinUser(
        @RequestBody signinReq: SigninReq
    ): Mono<ResponseEntity<AuthResponse>> = mono { ResponseEntity.ok(authService.signinUser(signinReq)) }
}