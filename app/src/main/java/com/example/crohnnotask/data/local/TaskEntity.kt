package com.example.crohnnotask.data.local

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.model.TaskPriority

@Entity(
    tableName = "tasks",
    indices = [Index(value = ["ownerEmail", "isCompleted", "priority", "scheduledDate", "scheduledTime"])]
)
data class TaskEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(defaultValue = "''") val ownerEmail: String,
    val title: String,
    val scheduledAt: String,
    val priority: Int,
    val isCompleted: Boolean,
    @ColumnInfo(defaultValue = "''") val description: String,
    val scheduledDate: String?,
    val scheduledTime: String?
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
    isCompleted = isCompleted,
    description = description,
    scheduledDate = scheduledDate,
    scheduledTime = scheduledTime
)

fun Task.toEntity(ownerEmail: String): TaskEntity = TaskEntity(
    id = id,
    ownerEmail = ownerEmail,
    title = title,
    scheduledAt = scheduledAt,
    priority = when (priority) {
        TaskPriority.HIGH -> 3
        TaskPriority.MEDIUM -> 2
        TaskPriority.LOW -> 1
    },
    isCompleted = isCompleted,
    description = description,
    scheduledDate = scheduledDate,
    scheduledTime = scheduledTime
)
