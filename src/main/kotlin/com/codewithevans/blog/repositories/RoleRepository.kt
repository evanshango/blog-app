package com.codewithevans.blog.repositories

import com.codewithevans.blog.entities.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RoleRepository: JpaRepository<Role, UUID> {

    fun findByNameIgnoreCase(name: String): Role?
}