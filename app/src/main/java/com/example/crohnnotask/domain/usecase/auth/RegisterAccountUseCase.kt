package com.example.crohnnotask.domain.usecase.auth

import com.example.crohnnotask.domain.auth.AuthFailure
import com.example.crohnnotask.domain.auth.AuthRepository
import com.example.crohnnotask.domain.auth.AuthResult
import java.util.Locale

class RegisterAccountUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        displayName: String,
        email: String,
        password: CharArray,
        confirmation: CharArray
    ): AuthResult {
        val cleanName = displayName.trim()
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        if (cleanName.length !in MIN_NAME_LENGTH..MAX_NAME_LENGTH) return AuthResult.Failure(AuthFailure.INVALID_NAME)
        if (!EMAIL_PATTERN.matches(cleanEmail)) return AuthResult.Failure(AuthFailure.INVALID_EMAIL)
        if (password.size < MIN_PASSWORD_LENGTH) return AuthResult.Failure(AuthFailure.PASSWORD_TOO_SHORT)
        if (password.size > MAX_PASSWORD_LENGTH) return AuthResult.Failure(AuthFailure.PASSWORD_TOO_LONG)
        if (!password.contentEquals(confirmation)) return AuthResult.Failure(AuthFailure.PASSWORD_MISMATCH)
        return repository.register(cleanName, cleanEmail, password)
    }

    private companion object {
        const val MIN_NAME_LENGTH = 2
        const val MAX_NAME_LENGTH = 60
        const val MIN_PASSWORD_LENGTH = 8
        const val MAX_PASSWORD_LENGTH = 128
        val EMAIL_PATTERN = Regex("^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?(?:\\.[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?)+$", RegexOption.IGNORE_CASE)
    }
}
