package co.kr.whitewave.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.kr.whitewave.data.model.Sound
import co.kr.whitewave.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayingSoundsBottomSheet(
    playingSounds: List<Sound>,
    onVolumeChange: (Sound, Float) -> Unit,
    onSoundRemove: (Sound) -> Unit,
    onSavePreset: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            playingSounds.forEach { sound ->
                PlayingSoundItem(
                    sound = sound,
                    onVolumeChange = onVolumeChange,
                    onRemove = { onSoundRemove(sound) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = onSavePreset,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("커스텀 저장")
            }
        }
    }
}

@Composable
private fun PlayingSoundItem(
    sound: Sound,
    onVolumeChange: (Sound, Float) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = sound.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = sound.name,
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = sound.volume,
                onValueChange = { onVolumeChange(sound, it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        IconButton(onClick = onRemove) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Remove sound"
            )
        }
    }
}