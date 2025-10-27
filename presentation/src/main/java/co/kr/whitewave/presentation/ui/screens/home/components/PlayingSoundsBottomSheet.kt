package co.kr.whitewave.presentation.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.kr.whitewave.domain.model.sound.Sound
import co.kr.whitewave.presentation.R
import co.kr.whitewave.presentation.util.formatForDisplay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayingSoundsBottomSheet(
    playingSounds: List<Sound>,
    isPlaying: Boolean,
    remainingTime: Duration?,
    onVolumeChange: (Sound, Float) -> Unit,
    onSoundRemove: (Sound) -> Unit,
    onTogglePlayback: () -> Unit,
    onSetTimer: (Duration) -> Unit,
    onCancelTimer: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A2332),
        dragHandle = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                BottomSheetDefaults.DragHandle(
                    color = Color(0xFF4A5A6A)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (playingSounds.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "재생 중인 사운드가 없습니다",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF8A9AAA)
                        )
                    }
                }
            } else {
                // 재생 중인 사운드 목록
                items(playingSounds) { sound ->
                    PlayingSoundCard(
                        sound = sound,
                        onVolumeChange = onVolumeChange
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 타이머 섹션
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Music stops in",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF8A9AAA),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // 타이머 표시
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = remainingTime?.formatForDisplay() ?: "00:00",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "⏳",
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = 36.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 타이머 버튼들
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TimerButton(
                            text = "+1m",
                            onClick = { onSetTimer(1.minutes) },
                            modifier = Modifier.weight(1f)
                        )
                        TimerButton(
                            text = "+15m",
                            onClick = { onSetTimer(15.minutes) },
                            modifier = Modifier.weight(1f)
                        )
                        TimerButton(
                            text = "+1h",
                            onClick = { onSetTimer(1.hours) },
                            modifier = Modifier.weight(1f)
                        )
                        TimerButton(
                            text = "OFF",
                            onClick = onCancelTimer,
                            isOff = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 재생/일시정지 버튼
                    FilledIconButton(
                        onClick = onTogglePlayback,
                        modifier = Modifier.size(80.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFF2962FF)
                        )
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                            ),
                            contentDescription = if (isPlaying) "일시정지" else "재생",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun PlayingSoundCard(
    sound: Sound,
    onVolumeChange: (Sound, Float) -> Unit
) {
    var sliderPosition by remember { mutableStateOf(sound.volume) }

    // 사운드별 색상 설정
    val cardColors = getSoundCardColors(sound.name)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColors.first
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 아이콘과 이름
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = getSoundIcon(sound.name)),
                        contentDescription = sound.name,
                        tint = cardColors.second,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = sound.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = cardColors.second
                    )
                }

                // 볼륨 숫자
                Text(
                    text = "${(sliderPosition * 100).toInt()}",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = cardColors.second
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 볼륨 슬라이더
            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    onVolumeChange(sound, it)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = cardColors.second,
                    activeTrackColor = cardColors.second,
                    inactiveTrackColor = Color(0xFF4A5A6A)
                )
            )
        }
    }
}

@Composable
private fun TimerButton(
    text: String,
    onClick: () -> Unit,
    isOff: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            containerColor = if (isOff) Color(0xFFB71C1C) else Color.Transparent,
            contentColor = if (isOff) Color.White else Color(0xFF64B5F6)
        ),
        border = if (isOff) null else androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFF64B5F6)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// 사운드 이름에 따라 아이콘 리소스 반환
private fun getSoundIcon(soundName: String): Int {
    return when (soundName.lowercase()) {
        "rain" -> R.drawable.ic_rain
        "ocean", "ocean waves" -> R.drawable.ic_ocean
        "fire", "campfire" -> R.drawable.ic_fire
        "forest" -> R.drawable.ic_forest
        "cafe", "café" -> R.drawable.ic_cafe
        "wind" -> R.drawable.ic_wind
        "white noise" -> R.drawable.ic_white_noise
        "fan" -> R.drawable.ic_fan
        else -> R.drawable.ic_sound_default
    }
}

// 사운드 이름에 따라 카드 색상 반환 (배경색, 콘텐츠 색상)
private fun getSoundCardColors(soundName: String): Pair<Color, Color> {
    return when (soundName.lowercase()) {
        "rain" -> Pair(Color(0xFF3D5A7C), Color(0xFF64B5F6))
        "campfire", "fire" -> Pair(Color(0xFF5D4037), Color(0xFFFF9800))
        "ocean", "ocean waves" -> Pair(Color(0xFF2C4A5F), Color(0xFF4FC3F7))
        "forest" -> Pair(Color(0xFF2E4A3A), Color(0xFF81C784))
        "cafe", "café" -> Pair(Color(0xFF4E342E), Color(0xFFBCAAA4))
        "wind" -> Pair(Color(0xFF37474F), Color(0xFFB0BEC5))
        "white noise" -> Pair(Color(0xFF424242), Color(0xFFBDBDBD))
        "fan" -> Pair(Color(0xFF263238), Color(0xFF78909C))
        else -> Pair(Color(0xFF3D5A7C), Color(0xFF64B5F6))
    }
}