package com.example.crohnnotask.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE ownerEmail = :ownerEmail ORDER BY isCompleted ASC, priority DESC, CASE WHEN scheduledDate IS NULL THEN 1 ELSE 0 END, scheduledDate ASC, CASE WHEN scheduledTime IS NULL THEN 1 ELSE 0 END, scheduledTime ASC, scheduledAt ASC, title COLLATE NOCASE ASC")
    fun observeAll(ownerEmail: String): Flow<List<TaskEntity>>

    @Upsert
    suspend fun upsert(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = CASE WHEN isCompleted = 0 THEN 1 ELSE 0 END WHERE id = :taskId AND ownerEmail = :ownerEmail")
    suspend fun toggleCompletion(ownerEmail: String, taskId: String)

    @Query("UPDATE tasks SET ownerEmail = :ownerEmail WHERE ownerEmail = ''")
    suspend fun assignUnownedTasks(ownerEmail: String)
}
