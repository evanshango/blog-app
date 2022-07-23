package com.codewithevans.blog.security

import com.codewithevans.blog.entities.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit.MINUTES
import java.util.*

@Service
class JwtService(
    @Value("\${app.jwt-secret}") val jwtSecret: String,
    @Value("\${app.expires-in}") val expiresIn: Long
) {
    fun generateToken(user: User): TokenProvider {
        val claims: MutableMap<String, Any> = HashMap()

        claims["roles"] =  user.roles?.map { it.name }?.toList() ?: listOf<String>()

        val builder = Jwts.builder().setClaims(claims).setSubject(user.email).setId(user.id.toString())
            .setIssuedAt(Date.from(Instant.now())).setExpiration(Date.from(Instant.now().plus(expiresIn, MINUTES)))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
        return TokenProvider(builder.compact())
    }

    fun getUsername(provider: TokenProvider): String {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(provider.token).body.subject
    }

    suspend fun getUsernameOrEmail(): String {
        return ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal.toString()
    }

    fun isValid(provider: TokenProvider, user: User?): Boolean {
        val claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(provider.token).body
        val unexpired = claims.expiration.after(Date.from(Instant.now()))
        return unexpired && (claims.subject == user?.email)
    }
}