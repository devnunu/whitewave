package co.kr.whitewave.presentation.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.kr.whitewave.domain.model.sound.Sound
import co.kr.whitewave.presentation.R

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
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(sortedSounds) { sound ->
            SoundCard(
                sound = sound,
                onSelect = onSoundSelect
            )
        }
    }
}

@Composable
private fun SoundCard(
    sound: Sound,
    onSelect: (Sound) -> Unit,
    modifier: Modifier = Modifier
) {
    // 배경색 애니메이션
    val backgroundColor by animateColorAsState(
        targetValue = if (sound.isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300),
        label = "backgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (sound.isSelected)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "contentColor"
    )

    // 글로우 효과를 위한 애니메이션
    val infiniteTransition = rememberInfiniteTransition(label = "glowTransition")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f) // 정사각형 형태
    ) {
        // 글로우 효과 (재생 중일 때만)
        if (sound.isSelected) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .alpha(glowAlpha),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {}
        }

        // 메인 카드
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (sound.isSelected) 6.dp else 2.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onSelect(sound) }
            ) {
                // 프리미엄 뱃지 (우측 상단)
                if (sound.isPremium) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_premium),
                            contentDescription = "Premium",
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(16.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // 재생/일시정지 아이콘
                    Icon(
                        painter = painterResource(
                            id = if (sound.isSelected) R.drawable.ic_pause else R.drawable.ic_play
                        ),
                        contentDescription = if (sound.isSelected) "일시정지" else "재생",
                        tint = contentColor,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 사운드 이름
                    Text(
                        text = sound.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

