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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.model.TaskPriority

private val Ink = Color(0xFF1E1B2E)
private val Violet = Color(0xFF6650A4)

@Composable
fun DashboardScreen(state: DashboardUiState, onTaskClicked: (String) -> Unit) {
    var showMessage by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 28.dp)) {
        Text("CROHNNOTASK", color = Violet, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(22.dp))
        Text("Hola, Alex 👋", color = Ink, fontSize = 29.sp, fontWeight = FontWeight.Bold)
        Text("Un paso a la vez. Hoy es un buen día.", color = Color(0xFF777486), fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Violet), shape = RoundedCornerShape(24.dp)) {
            Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("TU ENFOQUE DE HOY", color = Color(0xFFDCD3F5), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(Modifier.height(7.dp))
                    Text("${state.pendingCount} tareas pendientes", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }
                Surface(shape = CircleShape, color = Color(0x33FFFFFF), modifier = Modifier.size(48.dp)) {
                    BoxCenterText("${state.completedCount}/${state.tasks.size}")
                }
            }
        }
        Spacer(Modifier.height(28.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Tareas de hoy", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("VER TODAS", color = Violet, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            items(state.tasks, key = { it.id }) { task ->
                TaskCard(task = task, onClick = { onTaskClicked(task.id) })
            }
        }
        if (showMessage) Text("La creación de tareas estará disponible en la siguiente iteración.", color = Violet, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
        Button(onClick = { showMessage = !showMessage }, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Violet)) {
            Text("＋   Nueva tarea", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TaskCard(task: Task, onClick: () -> Unit) {
    val priorityColor = when (task.priority) {
        TaskPriority.HIGH -> Color(0xFFCC5261)
        TaskPriority.MEDIUM -> Color(0xFFE6A63A)
        TaskPriority.LOW -> Color(0xFF4B9B79)
    }

    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = if (task.isCompleted) Color(0xFFD8F0E5) else Color(0xFFF0EDFA), modifier = Modifier.size(40.dp)) {
                BoxCenterText(if (task.isCompleted) "✓" else "•", if (task.isCompleted) Color(0xFF39815F) else Violet)
            }
            Column(Modifier.weight(1f).padding(horizontal = 13.dp)) {
                Text(task.title, color = if (task.isCompleted) Color.Gray else Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(5.dp))
                Text(task.scheduledAt, color = Color(0xFF888696), fontSize = 12.sp)
            }
            Surface(shape = RoundedCornerShape(20.dp), color = priorityColor.copy(alpha = .12f)) {
                Text(task.priority.label, Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = priorityColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
