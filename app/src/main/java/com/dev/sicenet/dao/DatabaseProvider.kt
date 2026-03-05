package com.dev.sicenet.dao

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AcademicDatabase? = null

    fun getDatabase(context: Context): AcademicDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AcademicDatabase::class.java,
                        "academic_db"
                    ).build()
            INSTANCE = instance
            instance
        }
    }
}
