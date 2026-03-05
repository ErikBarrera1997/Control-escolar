package com.dev.sicenet.dao

class LocalSNRepository(private val dao: AcademicDao) {

    // Calificaciones finales
    suspend fun saveCalificacionesFinales(list: List<CalificacionFinalEntity>) =
        dao.insertCalificacionesFinales(list)

    suspend fun getCalificacionesFinales(): List<CalificacionFinalEntity> =
        dao.getCalificacionesFinales()

    // Calificaciones por unidades
    suspend fun saveCalificacionesUnidades(list: List<CalificacionUnidadEntity>) =
        dao.insertCalificacionesUnidades(list)

    suspend fun getCalificacionesUnidades(): List<CalificacionUnidadEntity> =
        dao.getCalificacionesUnidades()

    // Kardex
    suspend fun saveKardex(list: List<KardexEntity>) =
        dao.insertKardex(list)

    suspend fun getKardex(): List<KardexEntity> =
        dao.getKardex()

    // Carga académica
    suspend fun saveCargaAcademica(list: List<CargaAcademicaEntity>) =
        dao.insertCargaAcademica(list)

    suspend fun getCargaAcademica(): List<CargaAcademicaEntity> =
        dao.getCargaAcademica()
}

