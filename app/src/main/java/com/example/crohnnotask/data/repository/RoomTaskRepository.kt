package com.example.crohnnotask.data.repository

import com.example.crohnnotask.data.local.TaskDao
import com.example.crohnnotask.data.local.toDomain
import com.example.crohnnotask.data.local.toEntity
import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTaskRepository(private val taskDao: TaskDao) : TaskRepository {
    override fun observeTasks(ownerEmail: String): Flow<List<Task>> =
        taskDao.observeAll(ownerEmail).map { entities -> entities.map { it.toDomain() } }

    override suspend fun toggleCompletion(ownerEmail: String, taskId: String) {
        taskDao.toggleCompletion(ownerEmail, taskId)
    }

    override suspend fun saveTask(ownerEmail: String, task: Task) {
        taskDao.upsert(task.toEntity(ownerEmail))
    }
}
