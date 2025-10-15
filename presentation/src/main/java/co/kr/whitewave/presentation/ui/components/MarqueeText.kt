package co.kr.whitewave.presentation.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import kotlin.math.roundToInt

@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified,
    isAnimating: Boolean = true
) {
    if (!isAnimating) {
        // 정지 상태면 애니메이션 없이 표시
        Text(
            text = text,
            modifier = modifier.clipToBounds(),
            style = style,
            color = color,
            maxLines = 1
        )
    } else {
        // 재생 중이면 마키 애니메이션
        val infiniteTransition = rememberInfiniteTransition(label = "marquee")

        val offsetX by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 10000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "marqueeOffset"
        )

        Text(
            text = "$text    $text", // 반복 표시를 위해 텍스트 복제
            modifier = modifier
                .clipToBounds()
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val width = placeable.width / 2 // 원본 텍스트의 절반만 사용
                    layout(constraints.maxWidth, placeable.height) {
                        val xOffset = (width * offsetX).roundToInt()
                        placeable.placeRelative(xOffset, 0)
                    }
                },
            style = style,
            color = color,
            maxLines = 1
        )
    }
}
