package com.example.crohnnotask.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.usecase.ObserveTasksUseCase
import com.example.crohnnotask.domain.usecase.ToggleTaskCompletionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
) {
    val pendingCount: Int get() = tasks.count { !it.isCompleted }
    val completedCount: Int get() = tasks.count { it.isCompleted }
}

class DashboardViewModel(
    observeTasks: ObserveTasksUseCase,
    private val toggleTaskCompletion: ToggleTaskCompletionUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeTasks().collect { tasks ->
                _uiState.update { it.copy(tasks = tasks, isLoading = false) }
            }
        }
    }

    fun onTaskClicked(taskId: String) {
        viewModelScope.launch { toggleTaskCompletion(taskId) }
    }

    class Factory(
        private val observeTasks: ObserveTasksUseCase,
        private val toggleTaskCompletion: ToggleTaskCompletionUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(observeTasks, toggleTaskCompletion) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
