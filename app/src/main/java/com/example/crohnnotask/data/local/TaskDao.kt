package com.example.crohnnotask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, priority DESC, scheduledAt ASC, title COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Query("UPDATE tasks SET isCompleted = CASE WHEN isCompleted = 0 THEN 1 ELSE 0 END WHERE id = :taskId")
    suspend fun toggleCompletion(taskId: String)
}
