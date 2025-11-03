package co.kr.whitewave.presentation.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.kr.whitewave.domain.model.sound.Sound
import co.kr.whitewave.presentation.util.getIconForSound

@Composable
fun SoundGrid(
    sounds: List<Sound>,
    onSoundSelect: (Sound) -> Unit,
    onVolumeChange: (Sound, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // 프리미엄 사운드를 뒤로 배치하기 위한 정렬
    val sortedSounds = sounds.sortedBy { it.isPremium }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(sortedSounds) { sound ->
            SoundCard(
                sound = sound,
                onSelect = onSoundSelect,
                onVolumeChange = onVolumeChange
            )
        }
    }
}

@Composable
private fun SoundCard(
    sound: Sound,
    onSelect: (Sound) -> Unit,
    onVolumeChange: (Sound, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderValue by remember { mutableFloatStateOf(sound.volume) }
    val iconRes = getIconForSound(sound)

    // 테두리 색상 애니메이션
    val borderColor by animateColorAsState(
        targetValue = if (sound.isSelected)
            androidx.compose.ui.graphics.Color(0xFF00D9FF) // 밝은 청록색
        else
            androidx.compose.ui.graphics.Color(0xFF1E3A5F).copy(alpha = 0.3f),
        animationSpec = tween(300),
        label = "borderColor"
    )

    // 배경색 애니메이션
    val backgroundColor by animateColorAsState(
        targetValue = if (sound.isSelected)
            androidx.compose.ui.graphics.Color(0xFF1A3A5A) // 선택된 카드 배경
        else
            androidx.compose.ui.graphics.Color(0xFF1E3A5F), // 기본 카드 배경
        animationSpec = tween(300),
        label = "backgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (sound.isSelected)
            androidx.compose.ui.graphics.Color(0xFF00D9FF) // 밝은 청록색
        else
            androidx.compose.ui.graphics.Color.White,
        animationSpec = tween(300),
        label = "contentColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f) // 세로로 조금 더 긴 형태
            .clickable { onSelect(sound) },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (sound.isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 상단: 아이콘과 사운드 이름
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 아이콘
                    Icon(
                        imageVector = iconRes,
                        contentDescription = sound.name,
                        tint = contentColor,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 사운드 이름
                    Text(
                        text = sound.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Premium 텍스트
                    if (sound.isPremium) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Premium",
                            style = MaterialTheme.typography.bodySmall,
                            color = androidx.compose.ui.graphics.Color(0xFF00D9FF),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // 하단: 볼륨 슬라이더 (선택된 경우에만)
                if (sound.isSelected) {
                    Slider(
                        value = sliderValue,
                        onValueChange = {
                            sliderValue = it
                            onVolumeChange(sound, it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = androidx.compose.ui.graphics.Color(0xFF00D9FF),
                            activeTrackColor = androidx.compose.ui.graphics.Color(0xFF00D9FF),
                            inactiveTrackColor = androidx.compose.ui.graphics.Color(0xFF4A5A6A)
                        )
                    )
                }
            }
        }
    }
}

