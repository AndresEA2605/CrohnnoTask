package com.example.crohnnotask.data.repository

import com.example.crohnnotask.data.local.TaskDao
import com.example.crohnnotask.data.local.toDomain
import com.example.crohnnotask.data.local.toEntity
import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.model.TaskPriority
import com.example.crohnnotask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RoomTaskRepository(private val taskDao: TaskDao) : TaskRepository {
    private val seedMutex = Mutex()

    override fun observeTasks(): Flow<List<Task>> = flow {
        seedDemoTasksIfEmpty()
        emitAll(taskDao.observeAll().map { entities -> entities.map { it.toDomain() } })
    }

    override suspend fun toggleCompletion(taskId: String) {
        taskDao.toggleCompletion(taskId)
    }

    private suspend fun seedDemoTasksIfEmpty() = seedMutex.withLock {
        if (taskDao.count() == 0) {
            taskDao.insertAll(
                listOf(
                    Task("task-1", "Revisar entregable de Android", "Hoy · 10:30", TaskPriority.HIGH),
                    Task("task-2", "Preparar apuntes de arquitectura", "Hoy · 14:00", TaskPriority.MEDIUM),
                    Task("task-3", "Enviar correo al equipo", "Hoy · 17:00", TaskPriority.LOW)
                ).map { it.toEntity() }
            )
        }
    }
}
