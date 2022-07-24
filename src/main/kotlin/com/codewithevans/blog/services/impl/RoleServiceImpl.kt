package com.codewithevans.blog.services.impl

import com.codewithevans.blog.dtos.RoleDto
import com.codewithevans.blog.entities.Role
import com.codewithevans.blog.exceptions.ResourceExists
import com.codewithevans.blog.exceptions.ResourceNotFound
import com.codewithevans.blog.helpers.toRoleDto
import com.codewithevans.blog.repositories.RoleRepository
import com.codewithevans.blog.requests.RoleReq
import com.codewithevans.blog.services.RoleService
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class RoleServiceImpl(private val rolesRepository: RoleRepository) : RoleService {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun getRoles(): Flow<RoleDto> = rolesRepository.findAll(
        Sort.by(Sort.Direction.ASC, "createdAt")
    ).map { it.toRoleDto() }.asFlow()

    override suspend fun fetchRoleById(roleId: UUID): RoleDto = withContext(IO) {
        return@withContext checkRole(roleId).toRoleDto()
    }

    override suspend fun createRole(roleReq: RoleReq): RoleDto = withContext(IO) {
        val existingRoleName = rolesRepository.findByNameIgnoreCase(roleReq.name)

        if (existingRoleName != null) throw ResourceExists("Role with name ${roleReq.name} already exists")

        val newRole = Role(name = roleReq.name.uppercase(), description = roleReq.description)

        val savedRole = rolesRepository.save(newRole)

        logger.info("Role with name ${savedRole.name} created successfully | {}", CREATED.value())

        return@withContext savedRole.toRoleDto()
    }

    override suspend fun updateRole(roleId: UUID, roleReq: RoleReq): RoleDto = withContext(IO) {
        val existing = checkRole(roleId)

        existing.description = roleReq.description
        existing.updatedAt = LocalDateTime.now()

        logger.info("Role ${existing.name}'s description updated successfully | {}", OK.value())

        return@withContext rolesRepository.save(existing).toRoleDto()
    }

    override suspend fun deleteRole(roleId: UUID) = withContext(IO) {
        val existing = checkRole(roleId)
        logger.info("Role with name ${existing.name} deleted successfully | {}", OK.value())
        return@withContext rolesRepository.delete(existing)
    }

    private suspend fun checkRole(roleId: UUID): Role = withContext(IO) {
        return@withContext rolesRepository.findById(roleId).orElseThrow {
            ResourceNotFound("Role with roleId $roleId not found")
        }
    }
}