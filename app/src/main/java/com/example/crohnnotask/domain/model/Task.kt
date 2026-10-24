package com.example.crohnnotask.domain.model

enum class TaskPriority(val label: String) {
    HIGH("Alta"),
    MEDIUM("Media"),
    LOW("Baja")
}

data class Task(
    val id: String,
    val title: String,
    val scheduledAt: String,
    val priority: TaskPriority,
    val isCompleted: Boolean = false,
    val description: String = "",
    val scheduledDate: String? = null,
    val scheduledTime: String? = null
)
