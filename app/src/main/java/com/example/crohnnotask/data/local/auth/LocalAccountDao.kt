package com.example.crohnnotask.data.local.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalAccountDao {
    @Query("SELECT * FROM accounts WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): LocalAccountEntity?

    @Query("SELECT * FROM accounts WHERE email = :email LIMIT 1")
    fun observeByEmail(email: String): Flow<LocalAccountEntity?>

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(account: LocalAccountEntity): Long
}
