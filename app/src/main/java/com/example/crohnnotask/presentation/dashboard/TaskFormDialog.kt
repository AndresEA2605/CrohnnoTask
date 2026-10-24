package com.example.crohnnotask.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.model.TaskPriority

@Composable
fun TaskFormDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onSave: (id: String?, title: String, scheduledAt: String, priority: TaskPriority) -> Unit
) {
    var title by remember(task?.id) { mutableStateOf(task?.title.orEmpty()) }
    var scheduledAt by remember(task?.id) { mutableStateOf(task?.scheduledAt.orEmpty()) }
    var priority by remember(task?.id) { mutableStateOf(task?.priority ?: TaskPriority.MEDIUM) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Nueva tarea" else "Editar tarea") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tarea") },
                    placeholder = { Text("¿Qué necesitas hacer?") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = scheduledAt,
                    onValueChange = { scheduledAt = it },
                    label = { Text("Día y hora (opcional)") },
                    placeholder = { Text("Hoy · 18:00") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Prioridad")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TaskPriority.entries.forEach { option ->
                        FilterChip(
                            selected = priority == option,
                            onClick = { priority = option },
                            label = { Text(option.label) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(task?.id, title, scheduledAt, priority)
                    onDismiss()
                },
                enabled = title.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
