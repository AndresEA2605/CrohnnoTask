package com.example.crohnnotask.presentation.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.presentation.dashboard.TaskCard

@Composable
fun AllTasksScreen(
    tasks: List<Task>,
    onTaskClicked: (String) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Todas las tareas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("${tasks.size} en total", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, bottom = 18.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (tasks.isEmpty()) {
                item { Text("Todavía no hay tareas.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onClick = { onTaskClicked(task.id) }
                    )
                }
            }
        }
    }
}
