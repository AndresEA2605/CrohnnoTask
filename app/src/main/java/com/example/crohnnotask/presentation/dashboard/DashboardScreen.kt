package com.example.crohnnotask.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.model.TaskPriority

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    userName: String,
    onTaskClicked: (String) -> Unit,
    onSaveTask: (String?, String, String, TaskPriority) -> Unit,
    onStartVoice: () -> Unit,
    onViewAll: () -> Unit
) {
    val ink = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary
    var showTaskForm by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    Column(Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 28.dp)) {
        Text("CROHNNOTASK", color = primary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(22.dp))
        Text("Hola, ${userName.substringBefore(' ')} 👋", color = ink, fontSize = 29.sp, fontWeight = FontWeight.Bold)
        Text("Un paso a la vez. Hoy es un buen día.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))
        Card(colors = CardDefaults.cardColors(containerColor = primary), shape = RoundedCornerShape(24.dp)) {
            Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("TU ENFOQUE DE HOY", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .8f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(Modifier.height(7.dp))
                    Text("${state.pendingCount} tareas pendientes", color = MaterialTheme.colorScheme.onPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }
                Surface(shape = CircleShape, color = Color(0x33FFFFFF), modifier = Modifier.size(48.dp)) {
                    BoxCenterText("${state.completedCount}/${state.tasks.size}")
                }
            }
        }
        Spacer(Modifier.height(28.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Tareas de hoy", color = ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("VER TODAS", Modifier.clickable(onClick = onViewAll), color = primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            if (state.tasks.isEmpty()) {
                item {
                    Text("Aún no tienes tareas. Cuéntame qué necesitas hacer y la organizo por ti.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(state.tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onClick = { onTaskClicked(task.id) },
                        onEdit = {
                            editingTask = task
                            showTaskForm = true
                        }
                    )
                }
            }
        }
        Button(onClick = onStartVoice, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = primary)) {
            Text("🎙   Habla para crear una tarea", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
        OutlinedButton(onClick = {
            editingTask = null
            showTaskForm = true
        }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Crear manualmente") }
    }

    if (showTaskForm) {
        TaskFormDialog(
            task = editingTask,
            onDismiss = { showTaskForm = false },
            onSave = onSaveTask
        )
    }
}

@Composable
fun TaskCard(task: Task, onClick: () -> Unit, onEdit: (() -> Unit)? = null) {
    val priorityColor = when (task.priority) {
        TaskPriority.HIGH -> Color(0xFFCC5261)
        TaskPriority.MEDIUM -> Color(0xFFE6A63A)
        TaskPriority.LOW -> Color(0xFF4B9B79)
    }

    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = if (task.isCompleted) Color(0xFFD8F0E5) else MaterialTheme.colorScheme.primary.copy(alpha = .12f), modifier = Modifier.size(40.dp)) {
                BoxCenterText(if (task.isCompleted) "✓" else "•", if (task.isCompleted) Color(0xFF39815F) else MaterialTheme.colorScheme.primary)
            }
            Column(Modifier.weight(1f).padding(horizontal = 13.dp)) {
                Text(task.title, color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(5.dp))
                Text(task.scheduledAt, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                if (task.description.isNotBlank()) {
                    Text(
                        task.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(shape = RoundedCornerShape(20.dp), color = priorityColor.copy(alpha = .12f)) {
                    Text(task.priority.label, Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = priorityColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                onEdit?.let {
                    Text("Editar", Modifier.clickable(onClick = it).padding(top = 7.dp), color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun BoxCenterText(text: String, color: Color = Color.White) {
    androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(text, color = color, fontWeight = FontWeight.Bold)
    }
}
