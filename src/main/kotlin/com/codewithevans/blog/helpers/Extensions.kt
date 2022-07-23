package com.codewithevans.blog.helpers

import com.codewithevans.blog.dtos.CommentDto
import com.codewithevans.blog.dtos.PostDto
import com.codewithevans.blog.dtos.RoleDto
import com.codewithevans.blog.entities.Comment
import com.codewithevans.blog.entities.Post
import com.codewithevans.blog.entities.Role

fun Role.toRoleDto(): RoleDto {
    return RoleDto(
        id = id.toString(),
        name = name,
        description = description
    )
}

fun Post.toPostDto(): PostDto {
    return PostDto(
        id = id.toString(),
        title = title,
        slug = slug ?: "no_slug",
        content = content,
        author = "${user!!.firstName} ${user!!.lastName}",
        authorEmail = user!!.email,
        createdAt = createdAt,
        updatedAt = updatedAt,
        comments = comments.size
    )
}

fun Comment.toCommentDto(): CommentDto {
    return CommentDto(
        id = id.toString(),
        comment = comment,
        author = "${user!!.firstName} ${user!!.lastName}",
        authorEmail = user!!.email,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}