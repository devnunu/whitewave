package co.kr.whitewave.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ui/theme/Theme.kt
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF89B4FA),
    secondary = Color(0xFF74C7EC),
    tertiary = Color(0xFF89DCEB),
    background = Color(0xFF1E1E2E)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E66F5),
    secondary = Color(0xFF209FB5),
    tertiary = Color(0xFF04A5E5),
    background = Color(0xFFEFF1F5)
)

@Composable
fun WhiteWaveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}