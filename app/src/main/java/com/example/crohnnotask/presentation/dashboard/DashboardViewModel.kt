package com.example.crohnnotask.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.model.TaskPriority
import com.example.crohnnotask.domain.usecase.ObserveTasksUseCase
import com.example.crohnnotask.domain.usecase.CreateTaskFromVoiceUseCase
import com.example.crohnnotask.domain.usecase.SaveTaskUseCase
import com.example.crohnnotask.domain.usecase.ToggleTaskCompletionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class DashboardUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val feedbackMessage: String? = null
) {
    val pendingCount: Int get() = tasks.count { !it.isCompleted }
    val completedCount: Int get() = tasks.count { it.isCompleted }
}

class DashboardViewModel(
    private val ownerEmail: String,
    observeTasks: ObserveTasksUseCase,
    private val toggleTaskCompletion: ToggleTaskCompletionUseCase,
    private val saveTask: SaveTaskUseCase,
    private val createTaskFromVoice: CreateTaskFromVoiceUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeTasks(ownerEmail).collect { tasks ->
                _uiState.update { it.copy(tasks = tasks, isLoading = false) }
            }
        }
    }

    fun onTaskClicked(taskId: String) {
        viewModelScope.launch { toggleTaskCompletion(ownerEmail, taskId) }
    }

    fun onSaveTask(id: String?, title: String, scheduledAt: String, priority: TaskPriority) {
        viewModelScope.launch {
            val previousTask = id?.let { taskId -> _uiState.value.tasks.firstOrNull { it.id == taskId } }
            saveTask(
                ownerEmail,
                Task(
                    id = id ?: UUID.randomUUID().toString(),
                    title = title.trim(),
                    scheduledAt = scheduledAt.trim().ifBlank { "Sin horario" },
                    priority = priority,
                    isCompleted = previousTask?.isCompleted ?: false,
                    description = previousTask?.description.orEmpty(),
                    scheduledDate = previousTask?.scheduledDate,
                    scheduledTime = previousTask?.scheduledTime
                )
            )
        }
    }

    fun onVoiceTranscript(transcript: String) {
        viewModelScope.launch {
            val result = runCatching { createTaskFromVoice(ownerEmail, transcript) }
            val message = when {
                transcript.isBlank() -> "No alcancé a escuchar la tarea. Inténtalo otra vez."
                result.isFailure -> "No se pudo guardar la tarea. Inténtalo de nuevo."
                result.getOrNull() != null -> "Tarea creada: ${result.getOrNull()?.title} · ${result.getOrNull()?.scheduledAt}"
                else -> "No entendí la tarea. Prueba: mañana a las 3 llamar al dentista para confirmar la cita."
            }
            _uiState.update { it.copy(feedbackMessage = message) }
        }
    }

    fun onVoiceRecognitionUnavailable() {
        _uiState.update { it.copy(feedbackMessage = "Este dispositivo no tiene un servicio de reconocimiento de voz disponible.") }
    }

    fun onFeedbackShown() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    class Factory(
        private val ownerEmail: String,
        private val observeTasks: ObserveTasksUseCase,
        private val toggleTaskCompletion: ToggleTaskCompletionUseCase,
        private val saveTask: SaveTaskUseCase,
        private val createTaskFromVoice: CreateTaskFromVoiceUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(ownerEmail, observeTasks, toggleTaskCompletion, saveTask, createTaskFromVoice) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
