package com.example.crohnnotask.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 1, exportSchema = true)
abstract class CrohnnoTaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var instance: CrohnnoTaskDatabase? = null

        fun getInstance(context: Context): CrohnnoTaskDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CrohnnoTaskDatabase::class.java,
                    "crohnnotask.db"
                ).build().also { instance = it }
            }
    }
}
