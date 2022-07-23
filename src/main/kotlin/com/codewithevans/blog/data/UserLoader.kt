package com.codewithevans.blog.data

import com.codewithevans.blog.services.DataLoader
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class UserLoader(private val dataLoader: DataLoader) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String?) {
        try {
            runBlocking {
                dataLoader.apply {
                    seedRoles()
                    seedUser()
                }
            }
        } catch (ex: Exception) {
            logger.error("Error while seeding default roles and user {}", ex.message)
            throw RuntimeException(ex.localizedMessage)
        }
    }
}