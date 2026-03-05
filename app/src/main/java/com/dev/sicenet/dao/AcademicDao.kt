package com.dev.sicenet.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AcademicDao {

    // Calificaciones finales
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalificacionesFinales(list: List<CalificacionFinalEntity>)

    @Query("SELECT * FROM calificaciones_finales")
    suspend fun getCalificacionesFinales(): List<CalificacionFinalEntity>

    // Calificaciones por unidades
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalificacionesUnidades(list: List<CalificacionUnidadEntity>)

    @Query("SELECT * FROM calificaciones_unidades")
    suspend fun getCalificacionesUnidades(): List<CalificacionUnidadEntity>

    // Kardex
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKardex(list: List<KardexEntity>)

    @Query("SELECT * FROM kardex")
    suspend fun getKardex(): List<KardexEntity>

    // Carga académica
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCargaAcademica(list: List<CargaAcademicaEntity>)

    @Query("SELECT * FROM carga_academica")
    suspend fun getCargaAcademica(): List<CargaAcademicaEntity>
}

