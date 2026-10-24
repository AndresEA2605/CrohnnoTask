package com.example.crohnnotask.domain.usecase.auth

import com.example.crohnnotask.domain.auth.AuthRepository

class ObserveCurrentUserUseCase(repository: AuthRepository) {
    val currentUser = repository.currentUser
}
