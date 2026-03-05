package com.dev.sicenet.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dev.sicenet.dao.CalificacionFinalEntity
import com.dev.sicenet.dao.CalificacionUnidadEntity
import com.dev.sicenet.dao.CargaAcademicaEntity
import com.dev.sicenet.dao.KardexEntity
import com.dev.sicenet.dao.LocalSNRepository
import com.google.gson.Gson

class SaveAcademicDataWorker(
    context: Context,
    params: WorkerParameters,
    private val localRepository: LocalSNRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val tipo = inputData.getString("tipo") ?: return Result.failure()
            val json = inputData.getString("datos") ?: return Result.failure()

            when (tipo) {
                "finales" -> {
                    val list = Gson().fromJson(json, Array<CalificacionFinalEntity>::class.java).toList()
                    localRepository.saveCalificacionesFinales(list)
                }
                "unidades" -> {
                    val list = Gson().fromJson(json, Array<CalificacionUnidadEntity>::class.java).toList()
                    localRepository.saveCalificacionesUnidades(list)
                }
                "kardex" -> {
                    val list = Gson().fromJson(json, Array<KardexEntity>::class.java).toList()
                    localRepository.saveKardex(list)
                }
                "carga" -> {
                    val list = Gson().fromJson(json, Array<CargaAcademicaEntity>::class.java).toList()
                    localRepository.saveCargaAcademica(list)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("WORKER_SAVE", "Error guardando datos: ${e.message}")
            Result.failure()
        }
    }

}
