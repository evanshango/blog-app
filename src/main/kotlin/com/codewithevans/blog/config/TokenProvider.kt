package com.codewithevans.blog.config

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils

class TokenProvider(val token: String): AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {
    override fun getCredentials(): Any = token

    override fun getPrincipal(): Any = token
}