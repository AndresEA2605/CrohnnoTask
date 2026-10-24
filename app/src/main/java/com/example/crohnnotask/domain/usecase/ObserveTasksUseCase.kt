package com.example.crohnnotask.domain.usecase

import com.example.crohnnotask.domain.repository.TaskRepository

class ObserveTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(ownerEmail: String) = repository.observeTasks(ownerEmail)
}
