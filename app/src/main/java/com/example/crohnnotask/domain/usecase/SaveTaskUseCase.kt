package com.example.crohnnotask.domain.usecase

import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.repository.TaskRepository

class SaveTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(ownerEmail: String, task: Task) = repository.saveTask(ownerEmail, task)
}
