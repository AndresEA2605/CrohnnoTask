package com.example.crohnnotask.domain.voice

import com.example.crohnnotask.domain.model.TaskPriority
import java.text.Normalizer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ParsedVoiceTask(
    val title: String,
    val description: String,
    val date: LocalDate?,
    val time: LocalTime?,
    val priority: TaskPriority
)

/** Extracts common Spanish date/time phrases and keeps the remaining spoken details as a short task. */
class SpanishTaskParser {
    fun parse(utterance: String, today: LocalDate = LocalDate.now()): ParsedVoiceTask? {
        if (utterance.isBlank()) return null

        var remaining = utterance.trim()
        val timeMatch = numericTime.find(remaining) ?: spokenTime.find(remaining)
        val time = timeMatch?.let(::parseTime)
        if (timeMatch != null) remaining = remaining.replaceRange(timeMatch.range, " ")

        val (date, withoutDate) = extractDate(remaining, today)
        remaining = withoutDate

        var priority = TaskPriority.MEDIUM
        val explicitPriority = priorityPattern.find(remaining)
        if (explicitPriority != null) {
            val label = explicitPriority.groups[1]?.value ?: explicitPriority.groups[2]?.value.orEmpty()
            priority = priorityFrom(label)
            remaining = remaining.replaceRange(explicitPriority.range, " ")
        } else {
            val urgent = urgentPattern.find(remaining)
            if (urgent != null) {
                priority = TaskPriority.HIGH
                remaining = remaining.replaceRange(urgent.range, " ")
            }
        }

        remaining = removeCommandWords(remaining)
            .replace(Regex("[,:;.!?¿¡]+"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        if (remaining.isBlank()) return null

        val detailMatch = detailSeparator.find(remaining)
        var titleText = if (detailMatch != null) remaining.substring(0, detailMatch.range.first).trim() else remaining
        var descriptionText = detailMatch?.let {
            val detail = remaining.substring(it.range.last + 1).trim()
            if (detail.isBlank()) "" else "${it.groupValues[1].trim().replaceFirstChar(Char::uppercase)} $detail"
        }.orEmpty()

        val words = titleText.split(Regex("\\s+")).filter(String::isNotBlank)
        if (words.size > MAX_TITLE_WORDS) {
            titleText = words.take(MAX_TITLE_WORDS).joinToString(" ")
            val extraWords = words.drop(MAX_TITLE_WORDS).joinToString(" ")
            descriptionText = listOf(extraWords, descriptionText).filter(String::isNotBlank).joinToString(". ")
        }

        val title = sentenceCase(titleText)
        if (title.isBlank()) return null
        val description = when {
            descriptionText.isNotBlank() -> sentenceCase(descriptionText).take(MAX_DESCRIPTION_LENGTH)
            else -> "Completar: $title.".take(MAX_DESCRIPTION_LENGTH)
        }

        return ParsedVoiceTask(title, description, date, time, priority)
    }

    private fun parseTime(match: MatchResult): LocalTime? = runCatching {
        val hourText = match.groups[1]?.value ?: return null
        val minuteText = match.groups[2]?.value
        val suffix = match.groupValues.drop(3).firstOrNull { it.isNotEmpty() }.orEmpty().lowercase(Locale.ROOT)
        var hour = hourText.toIntOrNull() ?: spokenNumbers[normalize(hourText)] ?: return null
        var minute = minuteText?.toIntOrNull() ?: when (minuteText?.lowercase(Locale.ROOT)) {
            "media" -> 30
            "cuarto" -> 15
            else -> 0
        }
        if (suffix in setOf("pm", "p.m.", "tarde", "noche") && hour in 1..11) hour += 12
        if (suffix in setOf("am", "a.m.", "mañana") && hour == 12) hour = 0
        LocalTime.of(hour, minute)
    }.getOrNull()

    private fun extractDate(text: String, today: LocalDate): Pair<LocalDate?, String> {
        val relativeDays = relativeDaysPattern.find(text)
        if (relativeDays != null) {
            val count = relativeDays.groups[1]?.value?.toIntOrNull()
                ?: spokenNumbers[normalize(relativeDays.groups[2]?.value.orEmpty())]
            if (count != null) return today.plusDays(count.toLong()) to text.replaceRange(relativeDays.range, " ")
        }

        val dateWithMonth = dateWithMonthPattern.find(text)
        if (dateWithMonth != null) {
            val day = dateWithMonth.groups[1]?.value?.toIntOrNull()
            val month = monthNumbers[normalize(dateWithMonth.groups[2]?.value.orEmpty())]
            if (day != null && month != null) {
                val candidate = runCatching { LocalDate.of(today.year, month, day) }.getOrNull()
                if (candidate != null) {
                    val result = if (candidate.isBefore(today)) candidate.plusYears(1) else candidate
                    return result to text.replaceRange(dateWithMonth.range, " ")
                }
            }
        }

        val numericDate = numericDatePattern.find(text)
        if (numericDate != null) {
            val day = numericDate.groups[1]?.value?.toIntOrNull()
            val month = numericDate.groups[2]?.value?.toIntOrNull()
            val year = numericDate.groups[3]?.value?.toIntOrNull() ?: today.year
            if (day != null && month != null) {
                val candidate = runCatching { LocalDate.of(year, month, day) }.getOrNull()
                if (candidate != null) {
                    val result = if (numericDate.groups[3] == null && candidate.isBefore(today)) candidate.plusYears(1) else candidate
                    return result to text.replaceRange(numericDate.range, " ")
                }
            }
        }

        val relative = relativeDatePattern.find(text)
        if (relative != null) {
            val phrase = normalize(relative.value)
            val date = when (phrase) {
                "hoy" -> today
                "manana" -> today.plusDays(1)
                "pasado manana" -> today.plusDays(2)
                else -> null
            }
            if (date != null) return date to text.replaceRange(relative.range, " ")
        }

        val weekdayMatch = weekdayPattern.find(text)
        if (weekdayMatch != null) {
            val weekday = weekdayMap[normalize(weekdayMatch.groups[1]?.value.orEmpty())]
            if (weekday != null) {
                var daysAhead = (weekday.value - today.dayOfWeek.value + 7) % 7
                if (daysAhead == 0) daysAhead = 7
                if (normalize(weekdayMatch.value).contains("proximo") && daysAhead < 7) daysAhead += 7
                return today.plusDays(daysAhead.toLong()) to text.replaceRange(weekdayMatch.range, " ")
            }
        }

        return null to text
    }

    private fun removeCommandWords(text: String): String {
        var result = text.replace(commandPrefix, " ")
        result = result.replace(leadingFillers, " ")
        return result.replace(Regex("\\s+"), " ").trim()
    }

    private fun priorityFrom(value: String): TaskPriority = when (normalize(value)) {
        "alta" -> TaskPriority.HIGH
        "baja" -> TaskPriority.LOW
        else -> TaskPriority.MEDIUM
    }

    private fun sentenceCase(value: String): String {
        val normalized = value.replace(Regex("\\s+"), " ").trim().trim('.', ',', ';')
        return normalized.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "CO")) else it.toString() }
    }

    private fun normalize(value: String): String = Normalizer.normalize(value.lowercase(Locale("es", "CO")), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .replace('ñ', 'n')
        .trim()

    companion object {
        private const val MAX_TITLE_WORDS = 7
        private const val MAX_DESCRIPTION_LENGTH = 180

        private val numericTime = Regex(
            "\\b(?:a\\s+las?\\s+|sobre\\s+las?\\s+)?(\\d{1,2})[:.](\\d{2})\\s*(a\\.?m\\.?|p\\.?m\\.?|am|pm)?\\b",
            RegexOption.IGNORE_CASE
        )
        private val spokenTime = Regex(
            "\\b(?:a\\s+las?\\s+|sobre\\s+las?\\s+)(\\d{1,2}|una?|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez|once|doce)(?:\\s+y\\s+(media|cuarto))?(?:\\s+de\\s+la\\s+(mañana|tarde|noche)|\\s+(am|pm|a\\.?m\\.?|p\\.?m\\.?))?(?:\\s+horas?)?\\b",
            RegexOption.IGNORE_CASE
        )
        private val relativeDaysPattern = Regex("\\ben\\s+(\\d+)\\s+d[ií]as?\\b|\\ben\\s+(uno|una|dos|tres|cuatro|cinco|seis|siete)\\s+d[ií]as?\\b", RegexOption.IGNORE_CASE)
        private val dateWithMonthPattern = Regex("\\b(?:el\\s+)?(\\d{1,2})\\s+de\\s+(enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|setiembre|octubre|noviembre|diciembre)\\b", RegexOption.IGNORE_CASE)
        private val numericDatePattern = Regex("\\b(?:el\\s+)?(\\d{1,2})[/-](\\d{1,2})(?:[/-](\\d{4}))?\\b")
        private val relativeDatePattern = Regex("\\bpasado\\s+mañana\\b|\\bhoy\\b|(?<!de la )\\bmañana\\b", RegexOption.IGNORE_CASE)
        private val weekdayPattern = Regex("\\b(?:el\\s+|este\\s+|pr[oó]ximo\\s+)?(lunes|martes|mi[eé]rcoles|jueves|viernes|s[aá]bado|domingo)\\b", RegexOption.IGNORE_CASE)
        private val priorityPattern = Regex("\\bprioridad\\s+(alta|media|baja)\\b|\\b(alta|media|baja)\\s+prioridad\\b", RegexOption.IGNORE_CASE)
        private val urgentPattern = Regex("\\b(?:urgente|muy importante)\\b", RegexOption.IGNORE_CASE)
        private val detailSeparator = Regex("\\s+(para que|para|porque|ya que|con el fin de|con el objetivo de|debido a que)\\s+", RegexOption.IGNORE_CASE)
        private val commandPrefix = Regex("^\\s*(?:(?:oye|por favor)\\s+)*(?:recu[eé]rdame(?: que)?|recuerda(?:me)?(?: que)?|agenda(?:r)?|programa(?:r)?|crea(?:r)?|agrega(?:r)?|a[nñ]ade(?:r)?|apunta(?:r)?|anota(?:r)?|registra(?:r)?|tengo que|debo|necesito|quiero|me gustar[ií]a)\\s+", RegexOption.IGNORE_CASE)
        private val leadingFillers = Regex("^\\s*(?:una tarea (?:para )?|una actividad (?:para )?|que\\s+|por favor\\s+)+", RegexOption.IGNORE_CASE)

        private val spokenNumbers = mapOf(
            "una" to 1, "uno" to 1, "dos" to 2, "tres" to 3, "cuatro" to 4, "cinco" to 5,
            "seis" to 6, "siete" to 7, "ocho" to 8, "nueve" to 9, "diez" to 10, "once" to 11, "doce" to 12
        )
        private val monthNumbers = mapOf(
            "enero" to 1, "febrero" to 2, "marzo" to 3, "abril" to 4, "mayo" to 5, "junio" to 6,
            "julio" to 7, "agosto" to 8, "septiembre" to 9, "setiembre" to 9, "octubre" to 10,
            "noviembre" to 11, "diciembre" to 12
        )
        private val weekdayMap = mapOf(
            "lunes" to DayOfWeek.MONDAY, "martes" to DayOfWeek.TUESDAY, "miercoles" to DayOfWeek.WEDNESDAY,
            "jueves" to DayOfWeek.THURSDAY, "viernes" to DayOfWeek.FRIDAY, "sabado" to DayOfWeek.SATURDAY,
            "domingo" to DayOfWeek.SUNDAY
        )
    }
}
