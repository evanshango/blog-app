package com.codewithevans.blog.repositories

import com.codewithevans.blog.entities.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmailIgnoreCase(email: String): User?

    fun findAllByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCaseOrEmailContainsIgnoreCase(
        firstName: String, lastName: String, email: String, pageable: Pageable
    ): Page<User>
}