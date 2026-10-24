package com.example.crohnnotask.data.auth

import com.example.crohnnotask.data.local.TaskDao
import com.example.crohnnotask.data.local.TaskEntity
import com.example.crohnnotask.data.local.auth.LocalAccountDao
import com.example.crohnnotask.data.local.auth.LocalAccountEntity
import com.example.crohnnotask.domain.auth.AuthFailure
import com.example.crohnnotask.domain.auth.AuthResult
import com.example.crohnnotask.domain.auth.SessionStore
import com.example.crohnnotask.domain.usecase.auth.LoginUseCase
import com.example.crohnnotask.domain.usecase.auth.RegisterAccountUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomAuthRepositoryTest {
    @Test
    fun registrationLoginAndLogoutUseLocalHashedCredentials() = runBlocking {
        val accounts = FakeAccountDao()
        val tasks = FakeTaskDao()
        val session = FakeSessionStore()
        val repository = RoomAuthRepository(accounts, tasks, session, PasswordHasher(iterations = 2))
        val register = RegisterAccountUseCase(repository)
        val login = LoginUseCase(repository)

        val created = register(" Ana Pérez ", " ANA@example.com ", "claveSegura1".toCharArray(), "claveSegura1".toCharArray())
        assertTrue(created is AuthResult.Success)
        assertEquals("ana@example.com", accounts.records.keys.single())
        assertEquals("Ana Pérez", accounts.records.values.single().displayName)
        assertFalse(accounts.records.values.single().passwordHash == "claveSegura1")
        assertFalse(accounts.records.values.single().passwordSalt.isBlank())
        assertEquals("ana@example.com", session.activeEmail.value)
        assertEquals("ana@example.com", tasks.claimedTo)

        val duplicate = register("Ana", "ana@example.com", "claveSegura1".toCharArray(), "claveSegura1".toCharArray())
        assertEquals(AuthResult.Failure(AuthFailure.EMAIL_ALREADY_REGISTERED), duplicate)
        assertEquals(1, accounts.records.size)

        val incorrect = login("ana@example.com", "otraClave123".toCharArray())
        assertEquals(AuthResult.Failure(AuthFailure.INVALID_CREDENTIALS), incorrect)
        val accepted = login("ANA@example.com", "claveSegura1".toCharArray())
        assertTrue(accepted is AuthResult.Success)
        assertEquals("Ana Pérez", repository.currentUser.first()?.displayName)

        repository.logout()
        assertNull(session.activeEmail.value)
        assertNull(repository.currentUser.first())
    }

    @Test
    fun registrationRejectsBadEmailShortPasswordAndMismatch() = runBlocking {
        val repository = RoomAuthRepository(FakeAccountDao(), FakeTaskDao(), FakeSessionStore(), PasswordHasher(iterations = 2))
        val register = RegisterAccountUseCase(repository)

        assertEquals(AuthResult.Failure(AuthFailure.INVALID_EMAIL), register("Ana", "no-es-correo", "claveSegura1".toCharArray(), "claveSegura1".toCharArray()))
        assertEquals(AuthResult.Failure(AuthFailure.PASSWORD_TOO_SHORT), register("Ana", "ana@example.com", "corta".toCharArray(), "corta".toCharArray()))
        assertEquals(AuthResult.Failure(AuthFailure.PASSWORD_MISMATCH), register("Ana", "ana@example.com", "claveSegura1".toCharArray(), "otraClave123".toCharArray()))
    }

    private class FakeAccountDao : LocalAccountDao {
        val records = linkedMapOf<String, LocalAccountEntity>()
        override suspend fun findByEmail(email: String) = records[email]
        override fun observeByEmail(email: String): Flow<LocalAccountEntity?> = flowOf(records[email])
        override suspend fun count() = records.size
        override suspend fun insert(account: LocalAccountEntity): Long {
            if (records.containsKey(account.email)) return -1
            records[account.email] = account
            return records.size.toLong()
        }
    }

    private class FakeTaskDao : TaskDao {
        var claimedTo: String? = null
        override fun observeAll(ownerEmail: String): Flow<List<TaskEntity>> = flowOf(emptyList())
        override suspend fun upsert(task: TaskEntity) = Unit
        override suspend fun toggleCompletion(ownerEmail: String, taskId: String) = Unit
        override suspend fun assignUnownedTasks(ownerEmail: String) { claimedTo = ownerEmail }
    }

    private class FakeSessionStore : SessionStore {
        val activeEmail = MutableStateFlow<String?>(null)
        override suspend fun setActiveEmail(email: String) { activeEmail.value = email }
        override suspend fun clearActiveEmail() { activeEmail.value = null }
    }
}
