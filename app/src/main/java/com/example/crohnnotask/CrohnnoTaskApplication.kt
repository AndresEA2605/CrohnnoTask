package com.example.crohnnotask

import android.app.Application
import com.example.crohnnotask.data.local.CrohnnoTaskDatabase
import com.example.crohnnotask.data.repository.RoomTaskRepository
import com.example.crohnnotask.domain.usecase.ObserveTasksUseCase
import com.example.crohnnotask.domain.usecase.ToggleTaskCompletionUseCase

class CrohnnoTaskApplication : Application() {
    val container by lazy {
        val database = CrohnnoTaskDatabase.getInstance(this)
        val taskRepository = RoomTaskRepository(database.taskDao())
        AppContainer(
            observeTasks = ObserveTasksUseCase(taskRepository),
            toggleTaskCompletion = ToggleTaskCompletionUseCase(taskRepository)
        )
    }
}

data class AppContainer(
    val observeTasks: ObserveTasksUseCase,
    val toggleTaskCompletion: ToggleTaskCompletionUseCase
)
