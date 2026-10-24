package com.example.crohnnotask.data.repository

import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.model.TaskPriority
import com.example.crohnnotask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class InMemoryTaskRepository : TaskRepository {
    private val tasks = MutableStateFlow(
        listOf(
            Task("task-1", "Revisar entregable de Android", "Hoy · 10:30", TaskPriority.HIGH),
            Task("task-2", "Preparar apuntes de arquitectura", "Hoy · 14:00", TaskPriority.MEDIUM),
            Task("task-3", "Enviar correo al equipo", "Hoy · 17:00", TaskPriority.LOW)
        )
    )

    override fun observeTasks(): Flow<List<Task>> = tasks

    override suspend fun toggleCompletion(taskId: String) {
        tasks.update { current ->
            current.map { task ->
                if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
            }
        }
    }
}
