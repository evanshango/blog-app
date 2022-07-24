package com.codewithevans.blog.config

import com.codewithevans.blog.helpers.Constants
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

@Component
class AuthFailureHandler : ServerAuthenticationFailureHandler {

    @Autowired
    lateinit var mapper: ObjectMapper

    override fun onAuthenticationFailure(filter: WebFilterExchange?, ex: AuthenticationException?): Mono<Void> {
        val response = filter!!.exchange.response
        response.statusCode = UNAUTHORIZED
        response.headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        response.headers.set(HttpHeaders.WWW_AUTHENTICATE, "Bearer")

        val data = Constants.getResponseBody(ex?.message!!, UNAUTHORIZED)
        val buf = ByteBuffer.wrap(mapper.writeValueAsString(data).toByteArray(StandardCharsets.UTF_8))

        val buffer = response.bufferFactory().wrap(buf)
        return response.writeWith(Mono.just(buffer))
    }
}