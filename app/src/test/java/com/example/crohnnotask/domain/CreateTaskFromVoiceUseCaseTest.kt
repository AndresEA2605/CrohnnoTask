package com.example.crohnnotask.domain

import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.model.TaskPriority
import com.example.crohnnotask.domain.repository.TaskRepository
import com.example.crohnnotask.domain.usecase.CreateTaskFromVoiceUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class CreateTaskFromVoiceUseCaseTest {
    @Test
    fun naturalSpanishPhraseCreatesAndSavesStructuredTask() = runBlocking {
        val repository = FakeTaskRepository()
        val useCase = CreateTaskFromVoiceUseCase(
            repository = repository,
            currentDate = { LocalDate.of(2026, 10, 23) }
        )

        val task = useCase(
            ownerEmail = "ana@example.com",
            utterance = "Recuérdame mañana a las tres de la tarde llamar al dentista para confirmar la cita, prioridad alta."
        )

        assertNotNull(task)
        assertEquals("Llamar al dentista", task?.title)
        assertEquals("Para confirmar la cita", task?.description)
        assertEquals("2026-10-24", task?.scheduledDate)
        assertEquals("15:00", task?.scheduledTime)
        assertEquals(TaskPriority.HIGH, task?.priority)
        assertEquals("ana@example.com", repository.savedOwnerEmail)
        assertEquals(task, repository.savedTask)
    }

    @Test
    fun phraseWithoutAnActionDoesNotCreateTask() = runBlocking {
        val repository = FakeTaskRepository()
        val useCase = CreateTaskFromVoiceUseCase(repository)

        val task = useCase("ana@example.com", "mañana a las tres de la tarde")

        assertNull(task)
        assertNull(repository.savedTask)
    }

    private class FakeTaskRepository : TaskRepository {
        var savedTask: Task? = null
        var savedOwnerEmail: String? = null

        override fun observeTasks(ownerEmail: String): Flow<List<Task>> = flowOf(savedTask?.let { listOf(it) }.orEmpty())
        override suspend fun toggleCompletion(ownerEmail: String, taskId: String) = Unit
        override suspend fun saveTask(ownerEmail: String, task: Task) {
            savedOwnerEmail = ownerEmail
            savedTask = task
        }
    }
}
