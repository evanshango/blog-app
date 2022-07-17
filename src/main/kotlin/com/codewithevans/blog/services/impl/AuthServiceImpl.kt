package com.codewithevans.blog.services.impl

import com.codewithevans.blog.entities.BlogUser
import com.codewithevans.blog.exceptions.ResourceExists
import com.codewithevans.blog.exceptions.Unauthorized
import com.codewithevans.blog.repositories.UserRepository
import com.codewithevans.blog.requests.SigninReq
import com.codewithevans.blog.requests.SignupReq
import com.codewithevans.blog.responses.AuthResponse
import com.codewithevans.blog.security.JwtService
import com.codewithevans.blog.services.AuthService
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.CREATED
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthServiceImpl(
    @Value("\${app.pass-expiry}") val passExpiry: Long, private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder, private val jwtService: JwtService
) : AuthService {
    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun signupUser(signupReq: SignupReq): AuthResponse? = withContext(IO) {
        userRepository.findByEmailIgnoreCase(signupReq.email)?.let {
            throw ResourceExists("User already exists")
        }

        val toCreate = BlogUser(
            firstName = signupReq.firstName, lastName = signupReq.lastName, email = signupReq.email,
            password = passwordEncoder.encode(signupReq.password),
            passwordExpiry = LocalDateTime.now().plusDays(passExpiry)
        )

        val savedUser = userRepository.save(toCreate)

        logger.info("User {}'s account created successfully | {}", savedUser.email, CREATED.value())

        val token = jwtService.generateToken(savedUser)

        return@withContext AuthResponse(
            name = "${savedUser.firstName} ${savedUser.lastName}", email = savedUser.email, token = token.token
        )
    }

    override suspend fun signinUser(signinReq: SigninReq): AuthResponse? = withContext(IO) {
        var token = ""
        val user = userRepository.findByEmailIgnoreCase(signinReq.email)

        user?.let {
            if (passwordEncoder.matches(signinReq.password, it.password)) {
                token = jwtService.generateToken(it).token
                return@let
            } else {
                throw Unauthorized("User with email '${signinReq.email}' failed authentication")
            }
        }
        return@withContext AuthResponse(
            name = "${user!!.firstName} ${user.lastName}", email = user.email, token = token
        )
    }
}