package com.example.crohnnotask.data.local.auth

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class LocalAccountEntity(
    @PrimaryKey val email: String,
    val displayName: String,
    val passwordSalt: String,
    val passwordHash: String,
    val passwordIterations: Int,
    val createdAt: Long
)
