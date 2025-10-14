package co.kr.whitewave.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// 밝은 테마 컬러 스킴
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5D8BF4),         // 편안한 블루
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE6FF), // 부드러운 블루 컨테이너
    onPrimaryContainer = Color(0xFF001C3C),
    secondary = Color(0xFF7A97C9),       // 보조 블루
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDBE3F9),
    onSecondaryContainer = Color(0xFF101C36),
    tertiary = Color(0xFF5CBFA7),        // 편안한 티얼
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD0F1E8),
    onTertiaryContainer = Color(0xFF00201A),
    background = Color(0xFFF7F9FF),      // 부드러운 배경
    onBackground = Color(0xFF191C1D),
    surface = Color(0xFFFBFCFF),         // 깨끗한 표면
    onSurface = Color(0xFF191C1D),
    surfaceVariant = Color(0xFFE7EEFA),  // 배리언트 표면
    onSurfaceVariant = Color(0xFF41484D),
    error = Color(0xFFBA1A1A),
    outline = Color(0xFF72777F)
)

// 어두운 테마 컬러 스킴
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8BB0FF),         // 밝은 블루
    onPrimary = Color(0xFF002F65),
    primaryContainer = Color(0xFF004993), // 깊은 블루 컨테이너
    onPrimaryContainer = Color(0xFFD5E3FF),
    secondary = Color(0xFFAFC5FF),       // 보조 블루
    onSecondary = Color(0xFF0D2B5A),
    secondaryContainer = Color(0xFF254173),
    onSecondaryContainer = Color(0xFFDBE3FF),
    tertiary = Color(0xFF6CD6BC),        // 밝은 티얼
    onTertiary = Color(0xFF00382E),
    tertiaryContainer = Color(0xFF005144),
    onTertiaryContainer = Color(0xFFBFF8E6),
    background = Color(0xFF1A1C1E),      // 깊은 배경
    onBackground = Color(0xFFE1E2E5),
    surface = Color(0xFF121316),         // 매트한 표면
    onSurface = Color(0xFFE1E2E5),
    surfaceVariant = Color(0xFF252A2E),  // 배리언트 표면
    onSurfaceVariant = Color(0xFFC3C7CF),
    error = Color(0xFFFFB4AB),
    outline = Color(0xFF8B9198)
)

// 타이포그래피 정의
private val WhiteWaveTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// 모양 정의 (Chakra UI 스타일의 부드러운 모서리)
val WhiteWaveShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun WhiteWaveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,  // 동적 색상 지원
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // 상태 표시줄 색상 설정
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WhiteWaveTypography,
        shapes = WhiteWaveShapes,
        content = content
    )
}