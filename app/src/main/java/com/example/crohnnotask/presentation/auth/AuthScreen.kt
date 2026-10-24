package com.example.crohnnotask.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crohnnotask.domain.auth.AuthFailure

@Composable
fun AuthScreen(
    state: AuthUiState,
    onLogin: (email: String, password: String) -> Unit,
    onRegister: (name: String, email: String, password: String, confirmation: String) -> Unit,
    onClearError: () -> Unit
) {
    var registering by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    // Never place passwords in Compose's saved-instance-state bundle.
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp, vertical = 36.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("CROHNNOTASK", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(20.dp))
        Text(
            if (registering) "Crea tu cuenta" else "Inicia sesión",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            if (registering) "Regístrate para guardar tus tareas en este dispositivo." else "Ingresa a tu espacio de tareas.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        if (registering) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; onClearError() },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; onClearError() },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; onClearError() },
            label = { Text("Contraseña") },
            supportingText = if (registering) ({ Text("Usa entre 8 y 128 caracteres") }) else null,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(if (showPassword) "Ocultar" else "Mostrar")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (registering) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmation,
                onValueChange = { confirmation = it; onClearError() },
                label = { Text("Confirmar contraseña") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        state.error?.let { failure ->
            Spacer(Modifier.height(12.dp))
            Text(errorMessage(failure), color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                if (registering) onRegister(name, email, password, confirmation) else onLogin(email, password)
            },
            enabled = !state.isSubmitting && email.isNotBlank() && password.isNotBlank() && (!registering || name.isNotBlank() && confirmation.isNotBlank()),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isSubmitting) CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.height(20.dp))
            else Text(if (registering) "Crear cuenta" else "Iniciar sesión")
        }
        TextButton(
            onClick = {
                registering = !registering
                onClearError()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (registering) "Ya tengo cuenta · Iniciar sesión" else "¿No tienes cuenta? Regístrate")
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Tus credenciales se guardan localmente en este dispositivo. No se sincronizan con una cuenta en línea.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun errorMessage(failure: AuthFailure): String = when (failure) {
    AuthFailure.INVALID_NAME -> "Escribe un nombre de entre 2 y 60 caracteres."
    AuthFailure.INVALID_EMAIL -> "Ingresa un correo electrónico válido."
    AuthFailure.PASSWORD_TOO_SHORT -> "La contraseña debe tener al menos 8 caracteres."
    AuthFailure.PASSWORD_TOO_LONG -> "La contraseña puede tener hasta 128 caracteres."
    AuthFailure.PASSWORD_MISMATCH -> "Las contraseñas no coinciden."
    AuthFailure.EMAIL_ALREADY_REGISTERED -> "Ya existe una cuenta con ese correo en este dispositivo."
    AuthFailure.INVALID_CREDENTIALS -> "El correo o la contraseña no son correctos."
    AuthFailure.STORAGE_ERROR -> "No se pudo guardar el cambio. Inténtalo de nuevo."
}
