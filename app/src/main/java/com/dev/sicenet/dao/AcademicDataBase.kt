package com.dev.sicenet.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CalificacionFinalEntity::class,
        CalificacionUnidadEntity::class,
        KardexEntity::class,
        CargaAcademicaEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AcademicDatabase : RoomDatabase() {
    abstract fun academicDao(): AcademicDao


    companion object {
        @Volatile private var INSTANCE: AcademicDatabase? = null

        fun getInstance(context: Context): AcademicDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AcademicDatabase::class.java,
                    "academic_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

