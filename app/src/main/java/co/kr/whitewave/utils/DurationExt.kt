package co.kr.whitewave.utils

import kotlin.time.Duration

fun Duration.formatForDisplay(): String {
    val hours = inWholeHours
    val minutes = inWholeMinutes % 60

    return when {
        hours > 0 -> String.format("%02d:%02d:00", hours, minutes)
        else -> String.format("%02d:00", minutes)
    }
}