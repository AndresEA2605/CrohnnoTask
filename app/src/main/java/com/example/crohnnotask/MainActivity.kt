package com.example.crohnnotask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crohnnotask.presentation.dashboard.DashboardScreen
import com.example.crohnnotask.presentation.dashboard.DashboardViewModel

private val Violet = Color(0xFF6650A4)
private val Canvas = Color(0xFFF7F7FC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as CrohnnoTaskApplication).container

        setContent {
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.Factory(
                    container.observeTasks,
                    container.toggleTaskCompletion
                )
            )
            val uiState = dashboardViewModel.uiState.collectAsStateWithLifecycle().value

            MaterialTheme(colorScheme = lightColorScheme(primary = Violet, background = Canvas)) {
                Surface(modifier = Modifier.fillMaxSize(), color = Canvas) {
                    DashboardScreen(
                        state = uiState,
                        onTaskClicked = dashboardViewModel::onTaskClicked
                    )
                }
            }
        }
    }
}
