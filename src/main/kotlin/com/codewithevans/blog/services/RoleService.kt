package com.codewithevans.blog.services

import com.codewithevans.blog.dtos.RoleDto
import com.codewithevans.blog.requests.RoleReq
import reactor.core.publisher.Flux
import java.util.UUID

interface RoleService {

    fun getRoles(): Flux<RoleDto>

    suspend fun fetchRoleById(roleId: UUID): RoleDto

    suspend fun createRole(roleReq: RoleReq): RoleDto

    suspend fun updateRole(roleId: UUID, roleReq: RoleReq): RoleDto

    suspend fun deleteRole(roleId: UUID)
}