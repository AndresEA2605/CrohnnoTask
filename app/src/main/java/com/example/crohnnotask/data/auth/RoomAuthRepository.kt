package com.example.crohnnotask.data.auth

import com.example.crohnnotask.data.local.auth.LocalAccountDao
import com.example.crohnnotask.data.local.auth.LocalAccountEntity
import com.example.crohnnotask.data.local.TaskDao
import com.example.crohnnotask.domain.auth.AuthFailure
import com.example.crohnnotask.domain.auth.AuthRepository
import com.example.crohnnotask.domain.auth.AuthResult
import com.example.crohnnotask.domain.auth.LocalUser
import com.example.crohnnotask.domain.auth.SessionStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomAuthRepository(
    private val accountDao: LocalAccountDao,
    private val taskDao: TaskDao,
    private val sessionStore: SessionStore,
    private val passwordHasher: PasswordHasher = PasswordHasher()
) : AuthRepository {
    override val currentUser: Flow<LocalUser?> = sessionStore.activeEmail.flatMapLatest { email ->
        if (email == null) flowOf(null)
        else accountDao.observeByEmail(email).map { account -> account?.toDomain() }
    }

    override suspend fun register(displayName: String, email: String, password: CharArray): AuthResult {
        if (accountDao.findByEmail(email) != null) return AuthResult.Failure(AuthFailure.EMAIL_ALREADY_REGISTERED)
        val digest = withContext(Dispatchers.Default) { passwordHasher.hash(password) }
        val account = LocalAccountEntity(
            email = email,
            displayName = displayName,
            passwordSalt = digest.salt,
            passwordHash = digest.hash,
            passwordIterations = digest.iterations,
            createdAt = System.currentTimeMillis()
        )
        if (accountDao.insert(account) == -1L) return AuthResult.Failure(AuthFailure.EMAIL_ALREADY_REGISTERED)
        if (accountDao.count() == 1) taskDao.assignUnownedTasks(email)
        sessionStore.setActiveEmail(email)
        return AuthResult.Success(account.toDomain())
    }

    override suspend fun login(email: String, password: CharArray): AuthResult {
        val account = accountDao.findByEmail(email) ?: return AuthResult.Failure(AuthFailure.INVALID_CREDENTIALS)
        val digest = PasswordDigest(account.passwordSalt, account.passwordHash, account.passwordIterations)
        val accepted = withContext(Dispatchers.Default) { passwordHasher.verify(password, digest) }
        if (!accepted) return AuthResult.Failure(AuthFailure.INVALID_CREDENTIALS)
        sessionStore.setActiveEmail(email)
        return AuthResult.Success(account.toDomain())
    }

    override suspend fun logout() {
        sessionStore.clearActiveEmail()
    }

    private fun LocalAccountEntity.toDomain() = LocalUser(email = email, displayName = displayName)
}
