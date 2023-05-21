package com.example.login_app.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.login_app.domain.Repository
import com.example.login_app.core.Resource
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository): ViewModel() {
    fun login(email: String, password: String): LiveData<Resource<AuthResult>> {
        return liveData(Dispatchers.IO) {
            try {
                emit(repository.requestLogIn(email, password))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    fun signUp(email: String, password: String): LiveData<Resource<AuthResult>> {
        return liveData(Dispatchers.IO) {
            try {
                emit(repository.requestSignUp(email, password))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    fun passwordReset(email: String): LiveData<Resource<Void>> {
        return liveData(Dispatchers.IO) {
            try {
                emit(repository.requestPasswordReset(email))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    val signOut = liveData(Dispatchers.IO) {
        try {
            emit(repository.requestSignOut())
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }
}