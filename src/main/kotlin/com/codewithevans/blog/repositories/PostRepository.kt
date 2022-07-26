package com.codewithevans.blog.repositories

import com.codewithevans.blog.entities.Post
import com.codewithevans.blog.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PostRepository: JpaRepository<Post, UUID> {

    fun findByTitleIgnoreCase(title: String): Post?
    fun countAllByUser(user: User): Int

    fun findAllByUser(user: User): List<Post>
}