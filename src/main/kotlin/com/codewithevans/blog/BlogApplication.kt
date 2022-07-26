package com.codewithevans.blog

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(
    info = Info(
        title = "BLOG APP",
        version = "1.0",
        description = "API Documentation for Blog Application"
    ),
    servers = [Server(url = "/", description = "Blog Application")]
)
@SecurityScheme(
    name = "Jwt",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    `in` = SecuritySchemeIn.HEADER
)
class BlogApplication

fun main(args: Array<String>) {
    runApplication<BlogApplication>(*args)
}
