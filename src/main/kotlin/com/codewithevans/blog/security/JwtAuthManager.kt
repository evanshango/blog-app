package com.codewithevans.blog.security

import com.codewithevans.blog.config.TokenProvider
import com.codewithevans.blog.entities.Role
import com.codewithevans.blog.exceptions.InvalidBearerToken
import com.codewithevans.blog.repositories.UserRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@Component
class JwtAuthManager(private val jwt: JwtService, private val repo: UserRepository) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        return Mono.just(authentication!!).filter { it is TokenProvider }.cast(TokenProvider::class.java)
            .flatMap { jwt -> mono { validate(jwt) } }.onErrorMap { error -> InvalidBearerToken(error.message) }
    }

    private suspend fun validate(provider: TokenProvider): Authentication {
        val username = jwt.getUserName(provider)
        val user = withContext(IO) { repo.findByEmailIgnoreCase(username) }

        if (user != null) {
            if (jwt.isValid(provider, user)) {
                val roles = user.roles ?: HashSet()
                return UsernamePasswordAuthenticationToken(username, user.password, mapRolesToAuthority(roles))
            } else {
                throw IllegalArgumentException("Token provided is not valid.")
            }
        } else {
            throw UsernameNotFoundException("User with email $username not found")
        }
    }


    private fun mapRolesToAuthority(roles: Set<Role>): Collection<GrantedAuthority?>? {
        return roles.stream().map { role: Role -> SimpleGrantedAuthority(role.name) }.collect(Collectors.toList())
    }
}