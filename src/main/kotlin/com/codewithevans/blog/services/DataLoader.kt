package com.codewithevans.blog.services

interface DataLoader {
    suspend fun seedRoles()

    suspend fun seedUser()
}