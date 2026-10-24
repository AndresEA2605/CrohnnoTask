package com.example.crohnnotask.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<LocalUser?>
    suspend fun register(displayName: String, email: String, password: CharArray): AuthResult
    suspend fun login(email: String, password: CharArray): AuthResult
    suspend fun logout()
}

interface SessionStore {
    val activeEmail: Flow<String?>
    suspend fun setActiveEmail(email: String)
    suspend fun clearActiveEmail()
}
