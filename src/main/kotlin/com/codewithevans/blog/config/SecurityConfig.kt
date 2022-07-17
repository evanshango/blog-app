package com.codewithevans.blog.config

import com.codewithevans.blog.security.JwtAuthManager
import com.codewithevans.blog.security.JwtServerAuthConverter
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
class SecurityConfig {

    companion object {
        val OPEN_URLS = arrayOf(
            "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui.html/**",
            "/webjars/**", "/api/v1/auth/**"
        )

        val GET_OPEN_URLS = arrayOf(
            "/api/v1/roles/**", "/api/v1/posts/**", "/api/v1/comments/**"
        )
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityWebFilterChain(
        converter: JwtServerAuthConverter, http: ServerHttpSecurity, authManager: JwtAuthManager
    ): SecurityWebFilterChain {
        val filter = AuthenticationWebFilter(authManager)
        filter.setServerAuthenticationConverter(converter)

        return http
            .exceptionHandling().authenticationEntryPoint { exchange, _ ->
                Mono.fromRunnable {
                    exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                    exchange.response.headers.set(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                }
            }.and()
            .csrf().disable().cors().disable().formLogin().disable().httpBasic().disable()
            .authorizeExchange().pathMatchers(*OPEN_URLS).permitAll()
            .pathMatchers(HttpMethod.GET, *GET_OPEN_URLS).permitAll()
            .anyExchange().authenticated().and()
            .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}