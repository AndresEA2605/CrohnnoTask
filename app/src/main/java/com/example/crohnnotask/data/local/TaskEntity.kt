package com.example.crohnnotask.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.model.TaskPriority

@Entity(
    tableName = "tasks",
    indices = [Index(value = ["isCompleted", "priority", "scheduledAt"])]
)
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val scheduledAt: String,
    val priority: Int,
    val isCompleted: Boolean
)

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    scheduledAt = scheduledAt,
    priority = when (priority) {
        3 -> TaskPriority.HIGH
        2 -> TaskPriority.MEDIUM
        else -> TaskPriority.LOW
    },
    isCompleted = isCompleted
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    scheduledAt = scheduledAt,
    priority = when (priority) {
        TaskPriority.HIGH -> 3
        TaskPriority.MEDIUM -> 2
        TaskPriority.LOW -> 1
    },
    isCompleted = isCompleted
)
