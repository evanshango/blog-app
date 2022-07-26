package com.codewithevans.blog.repositories

import com.codewithevans.blog.entities.Comment
import com.codewithevans.blog.entities.Post
import com.codewithevans.blog.entities.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CommentRepository : JpaRepository<Comment, UUID> {

    fun countAllByPost(post: Post): Int

    fun findAllByPost(post: Post): List<Comment>

    fun findAllByPost(post: Post, pageable: Pageable): Page<Comment>
    fun countAllByUser(user: User): Int

    fun findAllByUser(user: User): List<Comment>
}