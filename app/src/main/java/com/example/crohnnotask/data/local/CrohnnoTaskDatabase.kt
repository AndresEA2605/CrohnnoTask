package com.example.crohnnotask.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.crohnnotask.data.local.auth.LocalAccountEntity
import com.example.crohnnotask.data.local.auth.LocalAccountDao

@Database(entities = [TaskEntity::class, LocalAccountEntity::class], version = 3, exportSchema = true)
abstract class CrohnnoTaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun accountDao(): LocalAccountDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN description TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE tasks ADD COLUMN scheduledDate TEXT")
                database.execSQL("ALTER TABLE tasks ADD COLUMN scheduledTime TEXT")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN ownerEmail TEXT NOT NULL DEFAULT ''")
                database.execSQL("DROP INDEX IF EXISTS index_tasks_isCompleted_priority_scheduledAt")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_ownerEmail_isCompleted_priority_scheduledDate_scheduledTime ON tasks(ownerEmail, isCompleted, priority, scheduledDate, scheduledTime)")
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS accounts (email TEXT NOT NULL, displayName TEXT NOT NULL, passwordSalt TEXT NOT NULL, passwordHash TEXT NOT NULL, passwordIterations INTEGER NOT NULL, createdAt INTEGER NOT NULL, PRIMARY KEY(email))"
                )
            }
        }

        @Volatile
        private var instance: CrohnnoTaskDatabase? = null

        fun getInstance(context: Context): CrohnnoTaskDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CrohnnoTaskDatabase::class.java,
                    "crohnnotask.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { instance = it }
            }
    }
}
