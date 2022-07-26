package com.codewithevans.blog.requests

import java.util.UUID

data class UserReq(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var roles: Set<UUID>? = null
)