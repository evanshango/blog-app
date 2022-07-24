package com.codewithevans.blog.services

import com.codewithevans.blog.dtos.RoleDto
import com.codewithevans.blog.requests.RoleReq
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RoleService {

    fun getRoles(): Flow<RoleDto>

    suspend fun fetchRoleById(roleId: UUID): RoleDto

    suspend fun createRole(roleReq: RoleReq): RoleDto

    suspend fun updateRole(roleId: UUID, roleReq: RoleReq): RoleDto

    suspend fun deleteRole(roleId: UUID)
}