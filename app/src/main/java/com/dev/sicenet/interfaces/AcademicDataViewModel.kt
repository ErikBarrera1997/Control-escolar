package com.dev.sicenet.interfaces

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.sicenet.dao.*
import com.dev.sicenet.data.SNRepository
import kotlinx.coroutines.launch

class AcademicViewModel(
    private val remoteRepository: SNRepository,
    private val localRepository: LocalSNRepository
) : ViewModel() {

    var califFinales by mutableStateOf<List<CalificacionFinalEntity>>(emptyList())
        private set
    var califUnidades by mutableStateOf<List<CalificacionUnidadEntity>>(emptyList())
        private set
    var kardex by mutableStateOf<List<KardexEntity>>(emptyList())
        private set
    var carga by mutableStateOf<List<CargaAcademicaEntity>>(emptyList())
        private set

    var lastUpdate by mutableStateOf<Long?>(null)
        private set

    /**
     * Carga datos académicos. Si hay internet, consulta remoto y guarda en local.
     * Si no hay internet, consulta directamente del repositorio local.
     */
    fun loadAcademicData(hasInternet: Boolean, matricula: String, password: String) {
        viewModelScope.launch {
            try {
                if (hasInternet) {
                    // Obtener perfil
                    val profile = remoteRepository.profile(matricula, password)

                    val finalesRemoto = remoteRepository.calificacionesFinales(profile.modEducativo)
                    val unidadesRemoto = remoteRepository.calificacionesUnidades()
                    val kardexRemoto = remoteRepository.cardex(profile.lineamiento)
                    val cargaRemoto = remoteRepository.cargaAcademica()

                    val now = System.currentTimeMillis()

                    // Guardar en local
                    localRepository.saveCalificacionesFinales(
                        finalesRemoto.map {
                            CalificacionFinalEntity(
                                id = 0,
                                materia = it.materia,
                                calificacion = it.calificacion,
                                periodo = it.periodo,
                                observaciones = it.observaciones,
                                fechaActualizacion = now
                            )
                        }
                    )

                    localRepository.saveCalificacionesUnidades(
                        unidadesRemoto.map {
                            CalificacionUnidadEntity(
                                id = 0,
                                materia = it.materia,
                                unidad = it.unidad,
                                calificacion = it.calificacion,
                                grupo = it.grupo,
                                observaciones = it.observaciones,
                                fechaActualizacion = now
                            )
                        }
                    )

                    localRepository.saveKardex(
                        kardexRemoto.map {
                            KardexEntity(
                                id = 0,
                                materia = it.materia,
                                calificacion = it.calificacion,
                                periodo = it.periodo,
                                promedio = it.promedio,
                                fechaActualizacion = now
                            )
                        }
                    )

                    localRepository.saveCargaAcademica(
                        cargaRemoto.map {
                            CargaAcademicaEntity(
                                id = 0,
                                materia = it.materia,
                                profesor = it.profesor,
                                horario = it.horario,
                                salon = it.salon,
                                observaciones = it.observaciones,
                                fechaActualizacion = now
                            )
                        }
                    )

                    // Actualizar estado en memoria
                    califFinales = localRepository.getCalificacionesFinales()
                    califUnidades = localRepository.getCalificacionesUnidades()
                    kardex = localRepository.getKardex()
                    carga = localRepository.getCargaAcademica()
                    lastUpdate = now



                } else {
                    // Sin internet cargar solo local
                    califFinales = localRepository.getCalificacionesFinales()
                    califUnidades = localRepository.getCalificacionesUnidades()
                    kardex = localRepository.getKardex()
                    carga = localRepository.getCargaAcademica()

                    lastUpdate = califFinales.firstOrNull()?.fechaActualizacion
                        ?: califUnidades.firstOrNull()?.fechaActualizacion
                                ?: kardex.firstOrNull()?.fechaActualizacion
                                ?: carga.firstOrNull()?.fechaActualizacion
                }
            } catch (e: Exception) {
                Log.e("AcademicVM", "Error cargando datos", e)
            }
        }
    }


}

