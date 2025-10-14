package co.kr.whitewave.presentation.ui.screens.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.kr.whitewave.presentation.R
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun CustomTimerDialog(
    onDismiss: () -> Unit,
    onSetTimer: (Duration) -> Unit
) {
    // 시간, 분, 초 상태 관리
    var hours by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }

    // 이전 값을 기록하여 증가/감소 방향 결정
    var previousHours by remember { mutableStateOf(0) }
    var previousMinutes by remember { mutableStateOf(0) }

    // 애니메이션 상태
    val isHoursAnimating = remember { mutableStateOf(false) }
    val isMinutesAnimating = remember { mutableStateOf(false) }

    // 시간을 추가/감소하는 함수
    fun addTime(amount: Int) {
        // 이전 값 저장
        previousHours = hours
        previousMinutes = minutes

        when {
            amount >= 60 -> {
                hours += amount / 60
                isHoursAnimating.value = true
            }
            amount >= 1 -> {
                minutes += amount
                if (minutes >= 60) {
                    hours += minutes / 60
                    minutes %= 60
                    isHoursAnimating.value = true
                }
                isMinutesAnimating.value = true
            }
        }
    }

    // 애니메이션 효과 리셋
    LaunchedEffect(isHoursAnimating.value) {
        if (isHoursAnimating.value) {
            delay(150) // 애니메이션 시간 단축
            isHoursAnimating.value = false
        }
    }

    LaunchedEffect(isMinutesAnimating.value) {
        if (isMinutesAnimating.value) {
            delay(150) // 애니메이션 시간 단축
            isMinutesAnimating.value = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 시간 디스플레이
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+",
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Light
                    )

                    // 시간
                    AnimatedTimeDigit(
                        value = hours,
                        previousValue = previousHours,
                        isAnimating = isHoursAnimating.value
                    )

                    Text(
                        text = ":",
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    // 분
                    AnimatedTimeDigit(
                        value = minutes,
                        previousValue = previousMinutes,
                        isAnimating = isMinutesAnimating.value
                    )
                }

                // 구분선
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // 시간 추가 버튼들
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 첫 번째 줄 (60분, 30분, 15분)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TimeAddButton(
                            text = "+60분",
                            onClick = { addTime(60) }
                        )
                        TimeAddButton(
                            text = "+30분",
                            onClick = { addTime(30) }
                        )
                        TimeAddButton(
                            text = "+15분",
                            onClick = { addTime(15) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 두 번째 줄 (10분, 5분, 1분)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TimeAddButton(
                            text = "+10분",
                            onClick = { addTime(10) }
                        )
                        TimeAddButton(
                            text = "+5분",
                            onClick = { addTime(5) }
                        )
                        TimeAddButton(
                            text = "+1분",
                            onClick = { addTime(1) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 하단 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 초기화 버튼
                    FilledTonalButton(
                        onClick = {
                            previousHours = hours
                            previousMinutes = minutes
                            hours = 0
                            minutes = 0
                            isHoursAnimating.value = true
                            isMinutesAnimating.value = true
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.filledTonalButtonColors(),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_reset),
                            contentDescription = "초기화",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // 저장 버튼
                    Button(
                        onClick = {
                            val duration = hours.hours + minutes.minutes
                            if (duration > Duration.ZERO) {
                                onSetTimer(duration)
                            }
                            onDismiss()
                        },
                        enabled = hours > 0 || minutes > 0,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .weight(3f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "저장",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedTimeDigit(
    value: Int,
    previousValue: Int,
    isAnimating: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.2f else 1.0f,
        animationSpec = tween(durationMillis = 150), // 애니메이션 시간 단축
        label = "scale"
    )

    // 이전 값과 현재 값을 비교하여 애니메이션 방향 결정
    val isIncreasing = value > previousValue

    AnimatedContent(
        targetState = value,
        transitionSpec = {
            if (isIncreasing) {
                // 증가하는 경우: 아래에서 위로 올라오는 애니메이션
                slideInVertically(animationSpec = tween(180)) { height -> height } togetherWith
                        slideOutVertically(animationSpec = tween(180)) { height -> -height }
            } else {
                // 감소하는 경우: 위에서 아래로 내려오는 애니메이션
                slideInVertically(animationSpec = tween(180)) { height -> -height } togetherWith
                        slideOutVertically(animationSpec = tween(180)) { height -> height }
            }
        },
        label = "digit"
    ) { targetValue ->
        Text(
            text = String.format("%02d", targetValue),
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.scale(scale)
        )
    }
}

@Composable
private fun TimeAddButton(
    text: String,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = Modifier
            .width(100.dp)
            .height(48.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}