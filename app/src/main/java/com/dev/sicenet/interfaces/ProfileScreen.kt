package com.dev.sicenet.interfaces

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavHostController
) {
    val state = viewModel.profileState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Perfil del Alumno", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.errorMessage?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }

        state.profile?.let { profile ->
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Text("Nombre: ${profile.nombre}") }
                item { Text("Matrícula: ${profile.matricula}") }
                item { Text("Carrera: ${profile.carrera}") }
                item { Text("Especialidad: ${profile.especialidad}") }
                item { Text("Semestre actual: ${profile.semActual}") }
                item { Text("Créditos acumulados: ${profile.cdtosAcumulados}") }
                item { Text("Créditos actuales: ${profile.cdtosActuales}") }
                item { Text("Estatus: ${profile.estatus}") }
                item { Text("Fecha de reinscripción: ${profile.fechaReins}") }
                item { Text("Modalidad educativa: ${profile.modEducativo}") }
                item { Text("Adeudo: ${if (profile.adeudo) "Sí" else "No"}") }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.reset()
                navController.navigate("login") {
                    popUpTo("profile/{matricula}/{contrasena}") { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
        }

    }
}



