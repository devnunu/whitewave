package co.kr.whitewave.presentation.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.kr.whitewave.data.model.sound.SoundEntity
import co.kr.whitewave.presentation.R
import co.kr.whitewave.presentation.util.getIconForSound

@Composable
fun SoundGrid(
    sounds: List<SoundEntity>,
    onSoundSelect: (SoundEntity) -> Unit,
    onVolumeChange: (SoundEntity, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // 프리미엄 사운드를 뒤로 배치하기 위한 정렬
    val sortedSounds = sounds.sortedBy { it.isPremium }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(sortedSounds) { sound ->
            SoundGridItem(
                sound = sound,
                onSelect = onSoundSelect,
                onVolumeChange = onVolumeChange,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SoundGridItem(
    sound: SoundEntity,
    onSelect: (SoundEntity) -> Unit,
    onVolumeChange: (SoundEntity, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // 상태에 따른 애니메이션 값
    val iconSize by animateFloatAsState(
        targetValue = if (sound.isSelected) 1.1f else 1.0f,
        animationSpec = tween(300)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (sound.isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300)
    )

    val contentColor by animateColorAsState(
        targetValue = if (sound.isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300)
    )

    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (sound.isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.Companion
                .clickable { onSelect(sound) }
                .padding(12.dp)
        ) {
            // 아이콘 컨테이너
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .padding(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = getIconForSound(sound)),
                    contentDescription = sound.name,
                    tint = contentColor,
                    modifier = Modifier
                        .scale(iconSize)
                        .size(36.dp)
                )

                // 프리미엄 뱃지
                if (sound.isPremium) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(20.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_premium),
                            contentDescription = "Premium",
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 사운드 이름
            Text(
                text = sound.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 볼륨 슬라이더
            Slider(
                value = sound.volume,
                onValueChange = { onVolumeChange(sound, it) },
                modifier = Modifier
                    .height(24.dp)
                    .width(80.dp),
                colors = SliderDefaults.colors(
                    thumbColor = contentColor,
                    activeTrackColor = contentColor,
                    inactiveTrackColor = contentColor.copy(alpha = 0.3f)
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(contentColor)
                    )
                }
            )
        }
    }
}