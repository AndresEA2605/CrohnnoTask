package com.example.crohnnotask.domain.repository

import com.example.crohnnotask.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    suspend fun toggleCompletion(taskId: String)
}
