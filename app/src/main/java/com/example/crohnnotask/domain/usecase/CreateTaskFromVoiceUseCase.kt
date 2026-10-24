package com.example.crohnnotask.domain.usecase

import com.example.crohnnotask.domain.model.Task
import com.example.crohnnotask.domain.repository.TaskRepository
import com.example.crohnnotask.domain.voice.SpanishTaskParser
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.util.Locale
import java.util.UUID

class CreateTaskFromVoiceUseCase(
    private val repository: TaskRepository,
    private val parser: SpanishTaskParser = SpanishTaskParser(),
    private val currentDate: () -> LocalDate = { LocalDate.now() }
) {
    suspend operator fun invoke(ownerEmail: String, utterance: String): Task? {
        val parsed = parser.parse(utterance, currentDate()) ?: return null
        val date = parsed.date?.toString()
        val time = parsed.time?.format(DateTimeFormatter.ofPattern("HH:mm"))
        val displayDate = parsed.date
            ?.format(DateTimeFormatter.ofPattern("EEE d MMM", Locale("es", "CO")))
            ?.replaceFirstChar { it.titlecase(Locale("es", "CO")) }
        val schedule = listOfNotNull(displayDate, time).joinToString(" · ").ifBlank { "Sin horario" }
        val task = Task(
            id = UUID.randomUUID().toString(),
            title = parsed.title,
            scheduledAt = schedule,
            priority = parsed.priority,
            description = parsed.description,
            scheduledDate = date,
            scheduledTime = time
        )
        repository.saveTask(ownerEmail, task)
        return task
    }
}
