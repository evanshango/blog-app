package com.codewithevans.blog.repositories

import com.codewithevans.blog.entities.Comment
import com.codewithevans.blog.entities.Reply
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReplyRepository : JpaRepository<Reply, UUID> {

    fun countAllByComment(comment: Comment): Int

    fun findAllByComment(comment: Comment): List<Reply>

    fun findAllByComment(comment: Comment, pageable: Pageable): Page<Reply>
}