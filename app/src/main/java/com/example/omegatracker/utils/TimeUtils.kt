package com.example.omegatracker.utils

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