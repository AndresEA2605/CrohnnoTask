package com.example.crohnnotask.domain.repository

import com.example.crohnnotask.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(ownerEmail: String): Flow<List<Task>>
    suspend fun toggleCompletion(ownerEmail: String, taskId: String)
    suspend fun saveTask(ownerEmail: String, task: Task)
}
