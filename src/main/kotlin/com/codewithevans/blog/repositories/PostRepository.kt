package com.codewithevans.blog.repositories

import com.codewithevans.blog.entities.Post
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PostRepository: JpaRepository<Post, UUID> {

    fun findByTitleIgnoreCase(title: String): Post?
}