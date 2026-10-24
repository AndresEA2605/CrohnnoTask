package com.example.crohnnotask

import android.app.Application
import com.example.crohnnotask.data.repository.InMemoryTaskRepository
import com.example.crohnnotask.domain.usecase.ObserveTasksUseCase
import com.example.crohnnotask.domain.usecase.ToggleTaskCompletionUseCase

class CrohnnoTaskApplication : Application() {
    val container by lazy {
        val taskRepository = InMemoryTaskRepository()
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
