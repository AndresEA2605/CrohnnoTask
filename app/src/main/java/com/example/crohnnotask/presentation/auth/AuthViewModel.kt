package com.example.crohnnotask.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.crohnnotask.domain.auth.AuthFailure
import com.example.crohnnotask.domain.auth.AuthResult
import com.example.crohnnotask.domain.auth.LocalUser
import com.example.crohnnotask.domain.usecase.auth.LoginUseCase
import com.example.crohnnotask.domain.usecase.auth.LogoutUseCase
import com.example.crohnnotask.domain.usecase.auth.ObserveCurrentUserUseCase
import com.example.crohnnotask.domain.usecase.auth.RegisterAccountUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

 data class AuthUiState(
    val currentUser: LocalUser? = null,
    val isSessionReady: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: AuthFailure? = null
)

class AuthViewModel(
    private val registerAccount: RegisterAccountUseCase,
    private val login: LoginUseCase,
    private val logout: LogoutUseCase,
    observeCurrentUser: ObserveCurrentUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeCurrentUser.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user, isSessionReady = true) }
            }
        }
    }

    fun onLogin(email: String, password: String) {
        submit {
            val chars = password.toCharArray()
            try {
                login(email, chars)
            } finally {
                chars.fill('\u0000')
            }
        }
    }

    fun onRegister(displayName: String, email: String, password: String, confirmation: String) {
        submit {
            val chars = password.toCharArray()
            val confirmChars = confirmation.toCharArray()
            try {
                registerAccount(displayName, email, chars, confirmChars)
            } finally {
                chars.fill('\u0000')
                confirmChars.fill('\u0000')
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { logout() }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update { state -> state.copy(error = AuthFailure.STORAGE_ERROR) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun submit(action: suspend () -> AuthResult) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            val result = try {
                Result.success(withContext(Dispatchers.IO) { action() })
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Result.failure(error)
            }
            _uiState.update { state ->
                state.copy(
                    isSubmitting = false,
                    error = result.getOrNull().let { authResult ->
                        when (authResult) {
                            is AuthResult.Failure -> authResult.reason
                            is AuthResult.Success -> null
                            null -> AuthFailure.STORAGE_ERROR
                        }
                    }
                )
            }
        }
    }

    class Factory(
        private val registerAccount: RegisterAccountUseCase,
        private val login: LoginUseCase,
        private val logout: LogoutUseCase,
        private val observeCurrentUser: ObserveCurrentUserUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(registerAccount, login, logout, observeCurrentUser) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
