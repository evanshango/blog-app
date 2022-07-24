package com.codewithevans.blog.services.impl

import com.codewithevans.blog.entities.Role
import com.codewithevans.blog.entities.User
import com.codewithevans.blog.helpers.Constants.admin_role
import com.codewithevans.blog.helpers.Constants.user_role
import com.codewithevans.blog.repositories.RoleRepository
import com.codewithevans.blog.repositories.UserRepository
import com.codewithevans.blog.services.DataLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.CREATED
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DataLoaderImpl(
    @Value("\${app.admin-user}") val email: String, @Value("\${app.admin-pass}") val pass: String,
    @Value("\${app.pass-expiry}") val passExpiry: Long, private val roleRepository: RoleRepository,
    private val userRepository: UserRepository, private val encoder: PasswordEncoder
) : DataLoader {
    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun seedRoles(): Unit = withContext(Dispatchers.IO) {
        val roles = roleRepository.findAll().toSet()
        if (roles.isEmpty()) {
            val newRoles = listOf(
                Role(name = admin_role.uppercase(), description = "The default admin role"),
                Role(name = user_role.uppercase(), description = "The default user role")
            )
            roleRepository.saveAll(newRoles)
        }
    }

    override suspend fun seedUser(): Unit = withContext(Dispatchers.IO) {
        val user = userRepository.findByEmailIgnoreCase(email)

        if (user == null) {
            roleRepository.findByNameIgnoreCase(admin_role)?.let {
                val adminUser = User(
                    firstName = "Mighty", lastName = "Admin", email = email, password = encoder.encode(pass),
                    passwordExpiry = LocalDateTime.now().plusDays(passExpiry), roles = setOf(it),
                    emailConfirmed = true, enableNotifications = true
                )
                userRepository.save(adminUser)
                logger.info("Default user seed was successful | {}", CREATED.value())
            }
        } else {
            logger.info("Default user already present")
        }
    }
}