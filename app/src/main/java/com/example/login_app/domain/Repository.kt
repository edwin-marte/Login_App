package com.example.login_app.domain

import com.example.login_app.core.Resource
import com.google.firebase.auth.AuthResult

interface Repository {
    suspend fun requestLogIn(userEmail: String, userPassword: String): Resource<AuthResult>
    suspend fun requestSignUp(userEmail: String, userPassword: String): Resource<AuthResult>
    suspend fun requestSignOut(): Resource<Unit>
    suspend fun requestPasswordReset(userEmail: String): Resource<Void>
}