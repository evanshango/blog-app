package com.codewithevans.blog.services.impl

import com.codewithevans.blog.entities.User
import com.codewithevans.blog.exceptions.*
import com.codewithevans.blog.helpers.Constants
import com.codewithevans.blog.repositories.RoleRepository
import com.codewithevans.blog.repositories.UserRepository
import com.codewithevans.blog.requests.PasswordReq
import com.codewithevans.blog.requests.SigninReq
import com.codewithevans.blog.requests.SignupReq
import com.codewithevans.blog.responses.AuthResponse
import com.codewithevans.blog.responses.PassResponse
import com.codewithevans.blog.security.JwtService
import com.codewithevans.blog.services.AuthService
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthServiceImpl(@Value("\${app.pass-expiry}") val passExpiry: Long) : AuthService {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    @Autowired
    lateinit var jwtService: JwtService

    override suspend fun signupUser(signupReq: SignupReq): AuthResponse? = withContext(IO) {
        userRepository.findByEmailIgnoreCase(signupReq.email)?.let {
            throw ResourceExists("User already exists")
        }

        val role = roleRepository.findByNameIgnoreCase(Constants.user_role)

        val toCreate = User(
            firstName = signupReq.firstName, lastName = signupReq.lastName, email = signupReq.email,
            password = encoder.encode(signupReq.password), roles = setOf(role!!),
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
            if (LocalDateTime.now().isAfter(it.passwordExpiry)) {
                throw NotPermitted("Your password has expired. Please reset")
            }
            if (encoder.matches(signinReq.password, it.password)) {
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

    override suspend fun resetPassword(passwordReq: PasswordReq): PassResponse? = withContext(IO) {
        val user = userRepository.findByEmailIgnoreCase(passwordReq.email) ?: throw ResourceNotFound(
            "User with email '${passwordReq.email}' not found"
        )

        if (passwordReq.oldPassword.isNotEmpty()) {
            if (!encoder.matches(passwordReq.oldPassword, user.password)) throw NotAcceptable(
                "The old password does not not match what we have in our database"
            )
        }

        user.password = encoder.encode(passwordReq.newPassword)
        user.passwordExpiry = LocalDateTime.now().plusDays(passExpiry)
        user.updatedAt = LocalDateTime.now()

        userRepository.save(user)

        return@withContext PassResponse(
            status = OK.toString(), message = "Password reset successful", timeStamp = LocalDateTime.now()
        )
    }
}