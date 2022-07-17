package com.codewithevans.blog.security

import com.codewithevans.blog.entities.Role
import com.codewithevans.blog.exceptions.InvalidBearerToken
import com.codewithevans.blog.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
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
class JwtAuthManager(
    private val jwtService: JwtService, private val userRepository: UserRepository
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        return Mono.justOrEmpty(authentication).filter { auth -> auth is TokenProvider }.cast(TokenProvider::class.java)
            .flatMap { jwt -> mono { validate(jwt) } }.onErrorMap { error -> InvalidBearerToken(error.message) }
    }

    private suspend fun validate(provider: TokenProvider): Authentication {
        val username = jwtService.getUsername(provider)
        val user = withContext(Dispatchers.IO) { userRepository.findByEmailIgnoreCase(username) }

        user?.let {
            if (jwtService.isValid(provider, it)) {
                val roles = user.roles ?: HashSet()
                return@let UsernamePasswordAuthenticationToken(username, user.password, mapRolesToAuthority(roles))
            } else {
                throw IllegalArgumentException("Token provided is not valid.")
            }
        }
        throw UsernameNotFoundException("User with email $username not found")
    }

    private fun mapRolesToAuthority(roles: Set<Role>): Collection<GrantedAuthority?>? {
        return roles.stream().map { role: Role -> SimpleGrantedAuthority(role.name) }.collect(Collectors.toList())
    }
}