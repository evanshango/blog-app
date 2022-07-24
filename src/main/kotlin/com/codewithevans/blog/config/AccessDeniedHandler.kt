package com.codewithevans.blog.config

import com.codewithevans.blog.helpers.Constants
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

@Component
class AccessDeniedHandler : ServerAccessDeniedHandler {

    @Autowired
    lateinit var mapper: ObjectMapper

    override fun handle(exchange: ServerWebExchange?, denied: AccessDeniedException?): Mono<Void> {
        val response = exchange!!.response
        response.statusCode = HttpStatus.FORBIDDEN
        response.headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        response.headers.set(HttpHeaders.WWW_AUTHENTICATE, "Bearer")

        val data = Constants.getResponseBody("Forbidden request", HttpStatus.FORBIDDEN)
        val buf = ByteBuffer.wrap(mapper.writeValueAsString(data).toByteArray(StandardCharsets.UTF_8))

        val buffer = response.bufferFactory().wrap(buf)
        return response.writeWith(Mono.just(buffer))
    }
}