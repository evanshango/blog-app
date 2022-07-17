package com.codewithevans.blog.repositories

import com.codewithevans.blog.entities.BlogUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<BlogUser, UUID> {

    fun findByEmailIgnoreCase(email: String): BlogUser?
}