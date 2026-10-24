package com.example.crohnnotask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crohnnotask.presentation.auth.AuthScreen
import com.example.crohnnotask.presentation.auth.AuthViewModel
import com.example.crohnnotask.presentation.dashboard.DashboardViewModel
import com.example.crohnnotask.presentation.navigation.CrohnnoTaskApp
import kotlinx.coroutines.launch

private val Violet = Color(0xFF6650A4)
private val LightCanvas = Color(0xFFF7F7FC)
private val DarkCanvas = Color(0xFF141218)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as CrohnnoTaskApplication).container

        setContent {
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModel.Factory(
                    container.registerAccount,
                    container.login,
                    container.logout,
                    container.observeCurrentUser
                )
            )
            val authState = authViewModel.uiState.collectAsStateWithLifecycle().value
            val isDarkTheme = container.userPreferences.isDarkTheme.collectAsStateWithLifecycle(initialValue = false).value
            val coroutineScope = rememberCoroutineScope()

            MaterialTheme(
                colorScheme = if (isDarkTheme) {
                    darkColorScheme(
                        primary = Color(0xFFD0BCFF),
                        background = DarkCanvas,
                        surface = Color(0xFF211F26),
                        onSurface = Color(0xFFE6E1E9),
                        onSurfaceVariant = Color(0xFFCAC4D0)
                    )
                } else {
                    lightColorScheme(primary = Violet, background = LightCanvas)
                }
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val user = authState.currentUser
                    when {
                        !authState.isSessionReady -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                        user == null -> AuthScreen(
                            state = authState,
                            onLogin = authViewModel::onLogin,
                            onRegister = authViewModel::onRegister,
                            onClearError = authViewModel::clearError
                        )
                        else -> {
                            val dashboardViewModel: DashboardViewModel = viewModel(
                                key = user.email,
                                factory = DashboardViewModel.Factory(
                                    user.email,
                                    container.observeTasks,
                                    container.toggleTaskCompletion,
                                    container.saveTask,
                                    container.createTaskFromVoice
                                )
                            )
                            val dashboardState = dashboardViewModel.uiState.collectAsStateWithLifecycle().value
                            CrohnnoTaskApp(
                                state = dashboardState,
                                currentUser = user,
                                onTaskClicked = dashboardViewModel::onTaskClicked,
                                onSaveTask = dashboardViewModel::onSaveTask,
                                onVoiceTranscript = dashboardViewModel::onVoiceTranscript,
                                onVoiceUnavailable = dashboardViewModel::onVoiceRecognitionUnavailable,
                                onFeedbackShown = dashboardViewModel::onFeedbackShown,
                                isDarkTheme = isDarkTheme,
                                onDarkThemeChanged = { enabled ->
                                    coroutineScope.launch { container.userPreferences.setDarkTheme(enabled) }
                                },
                                onLogout = authViewModel::onLogout
                            )
                        }
                    }
                }
            }
        }
    }
}
