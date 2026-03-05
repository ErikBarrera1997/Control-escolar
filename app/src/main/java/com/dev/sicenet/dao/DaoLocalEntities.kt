package com.dev.sicenet.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calificaciones_finales")
data class CalificacionFinalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materia: String,
    val calificacion: Double,
    val periodo: String,
    val observaciones: String,
    val fechaActualizacion: Long
)

@Entity(tableName = "calificaciones_unidades")
data class CalificacionUnidadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materia: String,
    val unidad: Int,
    val calificacion: Double,
    val grupo: String,
    val observaciones: String,
    val fechaActualizacion: Long
)

@Entity(tableName = "carga_academica")
data class CargaAcademicaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materia: String,
    val profesor: String,
    val horario: String,
    val salon: String,
    val observaciones: String,
    val fechaActualizacion: Long
)

@Entity(tableName = "kardex")
data class KardexEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materia: String,
    val calificacion: Double,
    val periodo: String,
    val promedio: Double,
    val fechaActualizacion: Long
)


