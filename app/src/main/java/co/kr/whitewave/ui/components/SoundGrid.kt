package co.kr.whitewave.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.kr.whitewave.R
import co.kr.whitewave.data.model.Sound

@Composable
fun SoundGrid(
    sounds: List<Sound>,
    onSoundSelect: (Sound) -> Unit,
    onVolumeChange: (Sound, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // 프리미엄 사운드를 뒤로 배치하기 위한 정렬
    // 별도 섹션으로 분리하지 않고 연속적으로 표시
    val sortedSounds = sounds.sortedBy { it.isPremium }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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

@Composable
private fun SoundGridItem(
    sound: Sound,
    onSelect: (Sound) -> Unit,
    onVolumeChange: (Sound, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onSelect(sound) }
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                painter = painterResource(id = sound.icon),
                contentDescription = sound.name,
                tint = if (sound.isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(48.dp)
            )

            if (sound.isPremium) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_premium),
                    contentDescription = "Premium",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }

        Text(
            text = sound.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Slider(
            value = sound.volume,
            onValueChange = { onVolumeChange(sound, it) },
            modifier = Modifier.width(80.dp)
        )
    }
}