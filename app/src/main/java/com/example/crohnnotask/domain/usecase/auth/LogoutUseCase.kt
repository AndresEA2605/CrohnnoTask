package com.example.crohnnotask.domain.usecase.auth

import com.example.crohnnotask.domain.auth.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}
