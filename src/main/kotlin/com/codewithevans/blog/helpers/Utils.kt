package com.codewithevans.blog.helpers

import com.codewithevans.blog.entities.User
import com.codewithevans.blog.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Utils {

    @Autowired
    lateinit var userRepository: UserRepository

    fun getUser(username: String): User? = userRepository.findByEmailIgnoreCase(username)
}