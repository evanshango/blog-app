package com.codewithevans.blog.security

import com.codewithevans.blog.entities.BlogUser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit.MINUTES
import java.util.*

@Service
class JwtService(
    @Value("\${app.jwt-secret}") val jwtSecret: String,
    @Value("\${app.expires-in}") val expiresIn: Long
) {
    fun generateToken(user: BlogUser): TokenProvider {
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

    fun isValid(provider: TokenProvider, user: BlogUser?): Boolean {
        val claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(provider.token).body
        val unexpired = claims.expiration.after(Date.from(Instant.now()))
        return unexpired && (claims.subject == user?.email)
    }
}