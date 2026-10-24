package com.example.crohnnotask.domain.usecase

import com.example.crohnnotask.domain.repository.TaskRepository

class ToggleTaskCompletionUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(ownerEmail: String, taskId: String) = repository.toggleCompletion(ownerEmail, taskId)
}
