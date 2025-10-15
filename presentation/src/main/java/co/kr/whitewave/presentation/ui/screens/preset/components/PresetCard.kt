package co.kr.whitewave.presentation.ui.screens.preset.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.kr.whitewave.domain.model.preset.PresetCategories
import co.kr.whitewave.domain.model.preset.PresetWithSounds
import co.kr.whitewave.presentation.R

/**
 * 프리셋 카드 UI 컴포넌트
 */
@Composable
fun PresetCard(
    preset: PresetWithSounds,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 카테고리에 따른 배경 이미지와 색상 결정
    val (backgroundResId, gradientColors) = when (preset.preset.category) {
        PresetCategories.SLEEP.category -> Pair(
            R.drawable.sample,  // 실제 앱에 맞는 이미지로 대체 필요
            listOf(Color(0xFF1E3B70).copy(alpha = 0.7f), Color(0xFF29539B).copy(alpha = 0.9f))
        )
        PresetCategories.RAIN.category -> Pair(
            R.drawable.sample,  // 실제 앱에 맞는 이미지로 대체 필요
            listOf(Color(0xFF1A4C6E).copy(alpha = 0.7f), Color(0xFF376E9A).copy(alpha = 0.9f))
        )
        PresetCategories.RELAX.category -> Pair(
            R.drawable.sample,  // 실제 앱에 맞는 이미지로 대체 필요
            listOf(Color(0xFF2E5D4B).copy(alpha = 0.7f), Color(0xFF347D64).copy(alpha = 0.9f))
        )
        PresetCategories.MEDITATION.category -> Pair(
            R.drawable.sample,  // 실제 앱에 맞는 이미지로 대체 필요
            listOf(Color(0xFF553C6E).copy(alpha = 0.7f), Color(0xFF7B579D).copy(alpha = 0.9f))
        )
        PresetCategories.WORK.category -> Pair(
            R.drawable.sample,  // 실제 앱에 맞는 이미지로 대체 필요
            listOf(Color(0xFF614C38).copy(alpha = 0.7f), Color(0xFF8A6E55).copy(alpha = 0.9f))
        )
        else -> Pair(
            R.drawable.sample,  // 실제 앱에 맞는 이미지로 대체 필요
            listOf(Color(0xFF505D6E).copy(alpha = 0.7f), Color(0xFF728399).copy(alpha = 0.9f))
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.0f)  // 정사각형 카드
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            hoveredElevation = 6.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 배경 이미지
            Image(
                painter = painterResource(id = backgroundResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 그라디언트 오버레이
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = gradientColors
                        )
                    )
            )

            // 컨텐츠 영역
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 상단 영역: 카테고리와 액션 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // 카테고리 표시
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = preset.preset.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // 프리미엄 뱃지
                    if (preset.preset.isPremium) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_premium),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Premium",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                    }
                }

                // 하단 영역: 프리셋 이름과 사운드 정보
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 사운드 개수 정보
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_music_note),
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${preset.sounds.size}개의 사운드",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 프리셋 이름
                    Text(
                        text = preset.preset.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )

                    // 커스텀 프리셋인 경우에만 편집/삭제 버튼 표시
                    if (!preset.preset.isDefault) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 삭제 버튼
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "삭제",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // 편집 버튼
                            IconButton(
                                onClick = onEdit,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "편집",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}