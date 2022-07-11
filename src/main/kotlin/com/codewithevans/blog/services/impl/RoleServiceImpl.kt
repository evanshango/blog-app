package com.codewithevans.blog.services.impl

import com.codewithevans.blog.dtos.RoleDto
import com.codewithevans.blog.requests.RoleReq
import com.codewithevans.blog.services.RoleService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.*

@Service
class RoleServiceImpl : RoleService {
    override fun getRoles(): Flux<RoleDto> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchRoleById(roleId: UUID): RoleDto {
        TODO("Not yet implemented")
    }

    override suspend fun createRole(roleReq: RoleReq): RoleDto {
        TODO("Not yet implemented")
    }

    override suspend fun updateRole(roleId: UUID, roleReq: RoleReq): RoleDto {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRole(roleId: UUID) {
        TODO("Not yet implemented")
    }
}