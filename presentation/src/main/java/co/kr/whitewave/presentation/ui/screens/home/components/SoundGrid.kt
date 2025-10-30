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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
    val iconRes = getIconForSound(sound)

    // 테두리 색상 애니메이션 (네온 블루/청록색)
    val borderColor by animateColorAsState(
        targetValue = if (sound.isSelected)
            androidx.compose.ui.graphics.Color(0xFF00D9FF) // 밝은 청록색
        else
            androidx.compose.ui.graphics.Color(0xFF1E3A5F).copy(alpha = 0.3f), // 어두운 파란색
        animationSpec = tween(300),
        label = "borderColor"
    )

    // 배경색 애니메이션
    val backgroundColor by animateColorAsState(
        targetValue = if (sound.isSelected)
            androidx.compose.ui.graphics.Color(0xFF1A3A5A) // 어두운 청록색 배경
        else
            androidx.compose.ui.graphics.Color(0xFF0F2744), // 매우 어두운 배경
        animationSpec = tween(300),
        label = "backgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (sound.isSelected)
            androidx.compose.ui.graphics.Color(0xFF00D9FF) // 밝은 청록색
        else
            androidx.compose.ui.graphics.Color(0xFF4A6B8A), // 중간 밝기의 회색-파란색
        animationSpec = tween(300),
        label = "contentColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f) // 정사각형 형태
    ) {
        // 메인 카드
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.large,
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
                    .clickable { onSelect(sound) }
                    .padding(8.dp)
            ) {
                // 우측 상단 일시정지 버튼 (활성화된 카드에만)
                if (sound.isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = androidx.compose.ui.graphics.Color(0xFF2A4A6A),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(20.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Pause,
                                contentDescription = "일시정지",
                                tint = contentColor,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // 프리미엄 뱃지 (우측 상단, 일시정지 버튼이 없을 때)
                if (sound.isPremium && !sound.isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WorkspacePremium,
                            contentDescription = "Premium",
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier
                                .padding(3.dp)
                                .size(12.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 사운드 아이콘
                    Icon(
                        imageVector = iconRes,
                        contentDescription = sound.name,
                        tint = contentColor,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 사운드 이름
                    Text(
                        text = sound.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
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

