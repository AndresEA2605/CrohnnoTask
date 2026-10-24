package com.example.crohnnotask.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    userName: String,
    userEmail: String,
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Configuración", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Personaliza CrohnnoTask", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))

        Text("Tu cuenta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text(userName, fontWeight = FontWeight.SemiBold)
                Text(userEmail, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onLogout) { Text("Cerrar sesión") }
            }
        }
        Spacer(Modifier.height(24.dp))

        Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface) {
            Row(
                Modifier.fillMaxWidth().padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f).padding(end = 16.dp)) {
                    Text("Modo oscuro", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("Guardar esta preferencia en el dispositivo", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onDarkThemeChanged,
                    modifier = Modifier.semantics { contentDescription = "Activar modo oscuro" }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Entrada por voz", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Text(
            "El dictado usa el servicio de reconocimiento de voz configurado en Android.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
