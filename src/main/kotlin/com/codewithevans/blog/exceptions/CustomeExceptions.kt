package com.codewithevans.blog.exceptions

class ResourceNotFound(message: String): RuntimeException(message)

class ResourceExists(message: String): RuntimeException(message)