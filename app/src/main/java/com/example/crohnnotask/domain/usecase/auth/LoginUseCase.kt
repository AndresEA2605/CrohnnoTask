package com.example.crohnnotask.domain.usecase.auth

import com.example.crohnnotask.domain.auth.AuthFailure
import com.example.crohnnotask.domain.auth.AuthRepository
import com.example.crohnnotask.domain.auth.AuthResult
import java.util.Locale

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: CharArray): AuthResult {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        if (!EMAIL_PATTERN.matches(cleanEmail)) return AuthResult.Failure(AuthFailure.INVALID_EMAIL)
        if (password.isEmpty()) return AuthResult.Failure(AuthFailure.INVALID_CREDENTIALS)
        if (password.size > MAX_PASSWORD_LENGTH) return AuthResult.Failure(AuthFailure.PASSWORD_TOO_LONG)
        return repository.login(cleanEmail, password)
    }

    private companion object {
        const val MAX_PASSWORD_LENGTH = 128
        val EMAIL_PATTERN = Regex("^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?(?:\\.[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?)+$", RegexOption.IGNORE_CASE)
    }
}
