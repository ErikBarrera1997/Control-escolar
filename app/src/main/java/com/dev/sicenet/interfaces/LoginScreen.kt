package com.dev.sicenet.interfaces

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onLoginSuccess: (String, String) -> Unit
) {
    val state = viewModel.loginState

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("SICENET ITSUR", style = MaterialTheme.typography.headlineMedium)
        Text("Bienvenido", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state.matricula,
            onValueChange = { viewModel.onMatriculaChanged(it) },
            label = { Text("Matrícula") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.contrasena,
            onValueChange = { viewModel.onContrasenaChanged(it) },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { viewModel.login() }) {
            Text("Ingresar")
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.errorMessage?.let {
            Text("Error: $it", color = Color.Red)
        }

        if (state.isSuccess && state.token.isNotBlank()) {
            //  dispara la navegación
            onLoginSuccess(state.matricula, state.contrasena)
        }
    }
}



