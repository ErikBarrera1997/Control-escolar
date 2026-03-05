package com.dev.sicenet.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dev.sicenet.dao.*
import com.dev.sicenet.data.NetworSNRepository
import com.dev.sicenet.network.ApiClient

class FetchAcademicDataWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val remoteRepository = NetworSNRepository(ApiClient.service)

    private val localRepository = LocalSNRepository(AcademicDatabase.getInstance(context).academicDao())

    override suspend fun doWork(): Result {
        return try {
            val tipo = inputData.getString("tipo") ?: return Result.failure()
            val matricula = inputData.getString("matricula") ?: ""
            val password = inputData.getString("password") ?: ""

            Log.d("Worker", "Parametros recibidos: tipo=$tipo, matricula=$matricula")

            val profile = remoteRepository.profile(matricula, password)
            Log.d("Worker", "Profile obtenido: $profile")

            val now = System.currentTimeMillis()

            when (tipo) {
                "finales" -> {
                    Log.d("Worker", "Solicitando calificaciones finales...")
                    val datos = remoteRepository.calificacionesFinales(profile.modEducativo)
                    Log.d("Worker", "Datos finales recibidos: ${datos.size}")
                    localRepository.saveCalificacionesFinales(
                        datos.map {
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
                    Log.d("Worker", "Finales guardados en Room")
                }
                "unidades" -> {
                    Log.d("Worker", "Solicitando calificaciones por unidades...")
                    val datos = remoteRepository.calificacionesUnidades()
                    Log.d("Worker", "Datos unidades recibidos: ${datos.size}")
                    localRepository.saveCalificacionesUnidades(
                        datos.map {
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
                    Log.d("Worker", "Unidades guardadas en Room")
                }
                "kardex" -> {
                    Log.d("Worker", "Solicitando kardex...")
                    val datos = remoteRepository.cardex(profile.lineamiento)
                    Log.d("Worker", "Datos kardex recibidos: ${datos.size}")
                    localRepository.saveKardex(
                        datos.map {
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
                    Log.d("Worker", "Kardex guardado en Room")
                }
                "carga" -> {
                    Log.d("Worker", "Solicitando carga académica...")
                    val datos = remoteRepository.cargaAcademica()
                    Log.d("Worker", "Datos carga recibidos: ${datos.size}")
                    localRepository.saveCargaAcademica(
                        datos.map {
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
                    Log.d("Worker", "Carga académica guardada en Room")
                }
            }

            Log.d("Worker", "doWork finalizado correctamente")
            return Result.success()

        } catch (e: Exception) {
            Log.e("Worker", "Error en FetchAcademicDataWorker en tipo=${inputData.getString("tipo")}", e)
            return Result.failure()
        }
    }

}

