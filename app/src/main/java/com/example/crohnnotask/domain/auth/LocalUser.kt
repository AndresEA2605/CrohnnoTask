package com.example.crohnnotask.domain.auth

data class LocalUser(
    val email: String,
    val displayName: String
)

enum class AuthFailure {
    INVALID_NAME,
    INVALID_EMAIL,
    PASSWORD_TOO_SHORT,
    PASSWORD_TOO_LONG,
    PASSWORD_MISMATCH,
    EMAIL_ALREADY_REGISTERED,
    INVALID_CREDENTIALS,
    STORAGE_ERROR
}

sealed interface AuthResult {
    data class Success(val user: LocalUser) : AuthResult
    data class Failure(val reason: AuthFailure) : AuthResult
}
