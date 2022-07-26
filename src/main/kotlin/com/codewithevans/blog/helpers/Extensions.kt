package com.codewithevans.blog.helpers

import com.codewithevans.blog.dtos.*
import com.codewithevans.blog.entities.*

fun Role.toRoleDto(): RoleDto {
    return RoleDto(
        id = id.toString(),
        name = name,
        description = description,
        createdAt = createdAt!!
    )
}

fun Post.toPostDto(commentsCount: Int): PostDto {
    return PostDto(
        id = id.toString(),
        title = title,
        slug = slug ?: "no_slug",
        content = content,
        author = "${user!!.firstName} ${user!!.lastName}",
        authorEmail = user!!.email,
        createdAt = createdAt,
        updatedAt = updatedAt,
        comments = commentsCount
    )
}

fun Comment.toCommentDto(repliesCount: Int): CommentDto {
    return CommentDto(
        id = id.toString(),
        comment = comment,
        author = "${user!!.firstName} ${user!!.lastName}",
        authorEmail = user!!.email,
        createdAt = createdAt!!,
        updatedAt = updatedAt,
        replies = repliesCount
    )
}

fun Reply.toReplyDto(): ReplyDto {
    return ReplyDto(
        id = id.toString(),
        reply = reply,
        author = "${user!!.firstName} ${user!!.lastName}",
        authorEmail = user!!.email,
        createdAt = createdAt!!,
        updatedAt = updatedAt
    )
}

fun User.toUserDto(postCount: Int, commentCount: Int, replyCount: Int): UserDto {
    return UserDto(
        id = id!!,
        firstName = firstName,
        lastName = lastName,
        email = email,
        roles = roles.let { roleSet -> roleSet?.map { it.toRoleDto() }?.toList() },
        posts = postCount,
        comments = commentCount,
        replies = replyCount,
        createdAt = createdAt
    )
}