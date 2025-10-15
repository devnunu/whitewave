package co.kr.whitewave.presentation.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.kr.whitewave.domain.model.sound.Sound
import co.kr.whitewave.presentation.R
import co.kr.whitewave.presentation.ui.components.WhiteWaveScaffold
import co.kr.whitewave.presentation.util.getIconForSound

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayingSoundsScreen(
    playingSounds: List<Sound>,
    onVolumeChange: (Sound, Float) -> Unit,
    onSoundRemove: (Sound) -> Unit,
    onSavePreset: () -> Unit,
    onBackClick: () -> Unit
) {
    WhiteWaveScaffold(
        topBar = {
            TopAppBar(
                title = { Text("재생 목록") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 재생 목록
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(playingSounds) { sound ->
                    SoundItem(
                        sound = sound,
                        onVolumeChange = { volume -> onVolumeChange(sound, volume) },
                        onRemove = { onSoundRemove(sound) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 프리셋 저장 버튼
            FilledTonalButton(
                onClick = onSavePreset,
                modifier = Modifier.fillMaxWidth(),
                enabled = playingSounds.isNotEmpty()
            ) {
                Text("현재 조합을 프리셋으로 저장")
            }
        }
    }
}

@Composable
private fun SoundItem(
    sound: Sound,
    onVolumeChange: (Float) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = getIconForSound(sound)),
                        contentDescription = sound.name,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.size(12.dp))

                    Text(
                        text = sound.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "제거",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 볼륨 슬라이더
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_volume_down),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Slider(
                    value = sound.volume,
                    onValueChange = onVolumeChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_volume_up),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
