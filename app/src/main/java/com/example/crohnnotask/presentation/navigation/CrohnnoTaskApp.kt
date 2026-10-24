package com.example.crohnnotask.presentation.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.crohnnotask.domain.model.TaskPriority
import com.example.crohnnotask.domain.auth.LocalUser
import com.example.crohnnotask.presentation.about.AboutScreen
import com.example.crohnnotask.presentation.dashboard.DashboardScreen
import com.example.crohnnotask.presentation.dashboard.DashboardUiState
import com.example.crohnnotask.presentation.settings.SettingsScreen
import com.example.crohnnotask.presentation.tasks.AllTasksScreen
import java.util.Locale

enum class AppDestination(val label: String, val symbol: String) {
    TODAY("Hoy", "⌂"),
    TASKS("Tareas", "☷"),
    SETTINGS("Ajustes", "⚙"),
    ABOUT("Acerca de", "ⓘ")
}

@Composable
fun CrohnnoTaskApp(
    state: DashboardUiState,
    currentUser: LocalUser,
    onTaskClicked: (String) -> Unit,
    onSaveTask: (String?, String, String, TaskPriority) -> Unit,
    onVoiceTranscript: (String) -> Unit,
    onVoiceUnavailable: () -> Unit,
    onFeedbackShown: () -> Unit,
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    var destination by rememberSaveable { mutableStateOf(AppDestination.TODAY) }
    val snackbarHostState = remember { SnackbarHostState() }

    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val transcript = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            onVoiceTranscript(transcript)
        }
    }

    LaunchedEffect(state.feedbackMessage) {
        state.feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            onFeedbackShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                AppDestination.entries.forEach { item ->
                    NavigationBarItem(
                        selected = destination == item,
                        onClick = { destination = item },
                        icon = {
                            Text(
                                item.symbol,
                                modifier = Modifier.semantics { contentDescription = item.label }
                            )
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        androidx.compose.foundation.layout.Box(
            Modifier.fillMaxSize().padding(contentPadding)
        ) {
            when (destination) {
                AppDestination.TODAY -> DashboardScreen(
                    state = state,
                    userName = currentUser.displayName,
                    onTaskClicked = onTaskClicked,
                    onSaveTask = onSaveTask,
                    onStartVoice = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.forLanguageTag("es-CO").toLanguageTag())
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Di la tarea, el día, la hora y para qué la necesitas")
                            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                        }
                        try {
                            speechLauncher.launch(intent)
                        } catch (_: ActivityNotFoundException) {
                            onVoiceUnavailable()
                        }
                    },
                    onViewAll = { destination = AppDestination.TASKS }
                )
                AppDestination.TASKS -> AllTasksScreen(
                    tasks = state.tasks,
                    onTaskClicked = onTaskClicked
                )
                AppDestination.SETTINGS -> SettingsScreen(
                    userName = currentUser.displayName,
                    userEmail = currentUser.email,
                    isDarkTheme = isDarkTheme,
                    onDarkThemeChanged = onDarkThemeChanged,
                    onLogout = onLogout
                )
                AppDestination.ABOUT -> AboutScreen()
            }
        }
    }
}
