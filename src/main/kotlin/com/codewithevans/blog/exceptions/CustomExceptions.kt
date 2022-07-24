package com.codewithevans.blog.exceptions

import org.springframework.security.core.AuthenticationException

class ResourceNotFound(message: String) : RuntimeException(message)
class ResourceExists(message: String) : RuntimeException(message)
class NotPermitted(message: String): RuntimeException(message)
class Unauthorized(message: String) : RuntimeException(message)
class InvalidBearerToken(message: String?) : AuthenticationException(message)