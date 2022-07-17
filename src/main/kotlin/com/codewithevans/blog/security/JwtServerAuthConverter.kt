package com.codewithevans.blog.security

import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtServerAuthConverter : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange?): Mono<Authentication> = Mono.justOrEmpty(
        exchange?.request?.headers?.getFirst(HttpHeaders.AUTHORIZATION)
    ).filter { it.startsWith("Bearer ") }.map { it.substring(7) }.map { TokenProvider(it) }
}