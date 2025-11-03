package co.kr.whitewave.presentation.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.kr.whitewave.domain.model.sound.Sound
import co.kr.whitewave.domain.model.sound.SoundCategory
import co.kr.whitewave.presentation.util.formatForDisplay
import co.kr.whitewave.presentation.util.getIconForSound
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1A2332),
        scrimColor = Color.Black.copy(alpha = 0.6f),
        windowInsets = WindowInsets(0, 0, 0, 0),
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                BottomSheetDefaults.DragHandle(
                    color = Color(0xFF4A5A6A),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Currently Playing",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 16.dp),
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
                        onVolumeChange = onVolumeChange,
                        onRemove = { onSoundRemove(sound) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // 타이머 섹션
                item {
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Set a Timer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // 타이머 버튼들
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TimerButton(
                            text = "+1m",
                            isSelected = false,
                            onClick = {
                                val newTime = (remainingTime ?: Duration.ZERO) + 1.minutes
                                onSetTimer(newTime)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TimerButton(
                            text = "+15m",
                            isSelected = false,
                            onClick = {
                                val newTime = (remainingTime ?: Duration.ZERO) + 15.minutes
                                onSetTimer(newTime)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TimerButton(
                            text = "+30m",
                            isSelected = false,
                            onClick = {
                                val newTime = (remainingTime ?: Duration.ZERO) + 30.minutes
                                onSetTimer(newTime)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TimerButton(
                            text = "+1hr",
                            isSelected = false,
                            onClick = {
                                val newTime = (remainingTime ?: Duration.ZERO) + 1.hours
                                onSetTimer(newTime)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 타이머 정보 카드
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(40.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF00D9FF).copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(vertical = 18.dp),
                                text = "Sounds will stop in ${remainingTime?.formatForDisplay() ?: "00:00"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF00D9FF)
                            )

                            if (remainingTime != null) {
                                androidx.compose.material3.TextButton(
                                    onClick = onCancelTimer,
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "Cancel",
                                        color = Color(0xFF00D9FF).copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Medium,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 재생/일시정지 버튼
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF00D9FF),
                                            Color(0xFF00C4E6)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .clickable(onClick = onTogglePlayback),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "일시정지" else "재생",
                                tint = Color(0xFF0F2023),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun PlayingSoundCard(
    sound: Sound,
    onVolumeChange: (Sound, Float) -> Unit,
    onRemove: () -> Unit
) {
    var sliderPosition by remember { mutableStateOf(sound.volume) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 상단: 아이콘 + 이름 + X 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 정사각형 둥근 아이콘 배경
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFF00D9FF).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconForSound(sound),
                        contentDescription = sound.name,
                        tint = Color(0xFF00D9FF),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = sound.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                // X 버튼
                androidx.compose.material3.IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 하단: 볼륨 슬라이더 + 퍼센트
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // 배경 트랙
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                    // 활성 트랙
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(sliderPosition)
                            .height(6.dp)
                            .background(
                                color = Color(0xFF00D9FF),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                    // 썸 (슬라이더를 위한 투명 오버레이)
                    Slider(
                        value = sliderPosition,
                        onValueChange = {
                            sliderPosition = it
                            onVolumeChange(sound, it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }

                Text(
                    text = "${(sliderPosition * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.width(40.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun TimerButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .background(
                color = if (isSelected) Color(0xFF00D9FF) else Color.White.copy(alpha = 0.1f),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF0F2023) else Color.White,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayingSoundsBottomSheetPreview() {
    val sampleSounds = listOf(
        Sound(
            id = "rain",
            name = "Gentle Rain",
            assetPath = "sounds/rain.mp3",
            category = SoundCategory.WEATHER,
            isPremium = false,
            isSelected = true,
            volume = 0.7f
        ),
        Sound(
            id = "fireplace",
            name = "Crackling Fire",
            assetPath = "sounds/fireplace.mp3",
            category = SoundCategory.HOME,
            isPremium = false,
            isSelected = true,
            volume = 0.5f
        )
    )

    PlayingSoundsBottomSheet(
        playingSounds = sampleSounds,
        isPlaying = true,
        remainingTime = 14.minutes + 28.seconds,
        onVolumeChange = { _, _ -> },
        onSoundRemove = { },
        onTogglePlayback = { },
        onSetTimer = { },
        onCancelTimer = { },
        onDismiss = { }
    )
}
