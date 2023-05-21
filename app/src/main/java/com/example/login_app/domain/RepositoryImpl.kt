package com.example.login_app.domain

import com.example.login_app.core.Resource
import com.example.login_app.core.safeCall
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RepositoryImpl @Inject constructor(): Repository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    override suspend fun requestLogIn(userEmail: String, userPassword: String): Resource<AuthResult> {
        return safeCall {
            val result = firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).await()
            Resource.Success(result)
        }
    }

    override suspend fun requestSignUp(userEmail: String, userPassword: String): Resource<AuthResult> {
        return safeCall {
            val result = firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).await()
            Resource.Success(result)
        }
    }

    override suspend fun requestSignOut(): Resource<Unit> {
        return safeCall {
            Resource.Success(firebaseAuth.signOut())
        }
    }

    override suspend fun requestPasswordReset(userEmail: String): Resource<Void> {
        return safeCall {
            val result = firebaseAuth.sendPasswordResetEmail(userEmail).await()
            Resource.Success(result)
        }
    }
}