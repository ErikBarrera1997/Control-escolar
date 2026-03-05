package com.dev.sicenet.interfaces

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun AcademicDataScreen(
    viewModel: AcademicViewModel,
    navController: NavController
) {
    val calificacionesFinales = viewModel.califFinales
    val calificacionesUnidades = viewModel.califUnidades
    val kardex = viewModel.kardex
    val cargaAcademica = viewModel.carga

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Datos Académicos", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Calificaciones Finales
        Text("Calificaciones Finales", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(calificacionesFinales) { calif ->
                Column {
                    Text("${calif.materia}: ${calif.calificacion} (${calif.periodo})")
                    if (calif.observaciones.isNotBlank()) {
                        Text("Obs: ${calif.observaciones}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calificaciones por Unidad
        Text("Calificaciones por Unidad", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(calificacionesUnidades) { unidad ->
                Column {
                    Text("${unidad.materia} - Unidad ${unidad.unidad}: ${unidad.calificacion}")
                    Text("Grupo: ${unidad.grupo}", style = MaterialTheme.typography.bodySmall)
                    if (unidad.observaciones.isNotBlank()) {
                        Text("Obs: ${unidad.observaciones}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kardex
        Text("Kardex", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(kardex) { k ->
                Column {
                    Text("${k.materia}: ${k.calificacion} (${k.periodo})")
                    Text("Promedio: ${k.promedio}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Carga Académica
        Text("Carga Académica", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(cargaAcademica) { c ->
                Column {
                    Text("${c.materia} - ${c.profesor}")
                    Text("Horario: ${c.horario} | Grupo: ${c.salon}", style = MaterialTheme.typography.bodySmall)
                    if (c.observaciones.isNotBlank()) {
                        Text("Obs: ${c.observaciones}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
        }
    }
}
