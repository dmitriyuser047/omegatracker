package com.example.omegatracker.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.abs
import kotlin.time.Duration

fun formatTimeDifference(estimatedTime: Duration, workedTime: Duration): String {
    val timeDifference = estimatedTime - workedTime
    val isNegative = timeDifference.isNegative()
    return formatDuration(timeDifference.absoluteValue, isNegative)
}

fun formatDuration(duration: Duration, isNegative: Boolean): String {
    val (hours, minutes, seconds) = duration.toComponents { hours, minutes, seconds, _ ->
        Triple(hours, minutes, seconds)
    }
    val formattedDuration = "%02d:%02d:%02d".format(hours, minutes, seconds)
    return if (isNegative) "-$formattedDuration" else formattedDuration
}

fun formatTimeLong(timestamp: Long): String {
    println(timestamp)
    val zonedDateTime = ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp),
        ZoneId.systemDefault()
    )
    return DateTimeFormatter.ofPattern("HH:mm").format(zonedDateTime)
}

fun formatTimeDifferenceShort(
    requiredTime: Duration,
    spentTime: Duration
): String {
    val difference = requiredTime - spentTime
    return difference.toComponents { hours, minutes, _, _ ->
        val formattedHours = if (hours < 0) {
            "-${abs(hours)}"
        } else {
            "$hours"
        }
        val formattedMinutes = "$minutes"

        "${formattedHours}ч ${formattedMinutes}м"
    }
}

fun toSimpleString(date: Date) : String {
    val format = SimpleDateFormat("dd.MM.yyy")
    return format.format(date)
}

fun formatDurationToGraph(durationMillis: Long): String {
    val totalMinutes = (durationMillis / (60 * 1000))

    val hours = totalMinutes / 60
    var rawMinutes = totalMinutes % 60

    rawMinutes = (rawMinutes / 5) * 5

    return "${hours}ч${if (rawMinutes < 10) "0" else ""}${rawMinutes}м"
}

fun parseTimeString(timeString: String): Float {
    val parts = timeString.split("ч", "м") // Разделяем строку на части
    val hours = parts[0].toFloatOrNull() ?: 0f // Часы
    val minutes = parts[1].toFloatOrNull() ?: 0f // Минуты
    return hours * 60 + minutes // Возвращаем общее количество минут
}