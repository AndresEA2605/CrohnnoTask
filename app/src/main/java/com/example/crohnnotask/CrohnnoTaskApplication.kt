package com.example.crohnnotask

import android.app.Application
import com.example.crohnnotask.data.local.CrohnnoTaskDatabase
import com.example.crohnnotask.data.preferences.UserPreferencesRepository
import com.example.crohnnotask.data.repository.RoomTaskRepository
import com.example.crohnnotask.data.auth.RoomAuthRepository
import com.example.crohnnotask.domain.usecase.ObserveTasksUseCase
import com.example.crohnnotask.domain.usecase.CreateTaskFromVoiceUseCase
import com.example.crohnnotask.domain.usecase.SaveTaskUseCase
import com.example.crohnnotask.domain.usecase.ToggleTaskCompletionUseCase
import com.example.crohnnotask.domain.usecase.auth.LoginUseCase
import com.example.crohnnotask.domain.usecase.auth.LogoutUseCase
import com.example.crohnnotask.domain.usecase.auth.ObserveCurrentUserUseCase
import com.example.crohnnotask.domain.usecase.auth.RegisterAccountUseCase

class CrohnnoTaskApplication : Application() {
    val container by lazy {
        val database = CrohnnoTaskDatabase.getInstance(this)
        val taskRepository = RoomTaskRepository(database.taskDao())
        val userPreferences = UserPreferencesRepository(this)
        val authRepository = RoomAuthRepository(database.accountDao(), database.taskDao(), userPreferences)
        AppContainer(
            observeTasks = ObserveTasksUseCase(taskRepository),
            toggleTaskCompletion = ToggleTaskCompletionUseCase(taskRepository),
            saveTask = SaveTaskUseCase(taskRepository),
            createTaskFromVoice = CreateTaskFromVoiceUseCase(taskRepository),
            userPreferences = userPreferences,
            registerAccount = RegisterAccountUseCase(authRepository),
            login = LoginUseCase(authRepository),
            logout = LogoutUseCase(authRepository),
            observeCurrentUser = ObserveCurrentUserUseCase(authRepository)
        )
    }
}

data class AppContainer(
    val observeTasks: ObserveTasksUseCase,
    val toggleTaskCompletion: ToggleTaskCompletionUseCase,
    val saveTask: SaveTaskUseCase,
    val createTaskFromVoice: CreateTaskFromVoiceUseCase,
    val userPreferences: UserPreferencesRepository,
    val registerAccount: RegisterAccountUseCase,
    val login: LoginUseCase,
    val logout: LogoutUseCase,
    val observeCurrentUser: ObserveCurrentUserUseCase
)
