package com.codewithevans.blog.services

import com.codewithevans.blog.requests.SigninReq
import com.codewithevans.blog.requests.SignupReq
import com.codewithevans.blog.responses.AuthResponse

interface AuthService {

    suspend fun signupUser(signupReq: SignupReq): AuthResponse?

    suspend fun signinUser(signinReq: SigninReq): AuthResponse?
}