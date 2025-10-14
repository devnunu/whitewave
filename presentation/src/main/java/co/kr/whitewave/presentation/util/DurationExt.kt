package co.kr.whitewave.presentation.util

import kotlin.time.Duration

fun Duration.formatForDisplay(): String {
    val hours = inWholeHours
    val minutes = (inWholeMinutes % 60)
    val seconds = (inWholeSeconds % 60)

    return when {
        hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
        else -> String.format("%02d:%02d", minutes, seconds)
    }
}