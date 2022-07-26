package com.codewithevans.blog.services

import com.codewithevans.blog.dtos.PaginationDto
import com.codewithevans.blog.dtos.UserDto
import com.codewithevans.blog.requests.UserReq
import java.util.*

interface UserService {

    suspend fun fetchUsers(
        pageNo: Int?, pageSize: Int?, orderBy: String?, orderDir: String?, search: String?
    ): PaginationDto<UserDto>

    suspend fun fetchUserById(userId: UUID): UserDto?

    suspend fun updateUser(userId: UUID, userReq: UserReq): UserDto?

    suspend fun deleteUser(userId: UUID)
}