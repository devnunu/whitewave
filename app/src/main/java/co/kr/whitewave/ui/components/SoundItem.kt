package co.kr.whitewave.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.kr.whitewave.R
import co.kr.whitewave.data.model.Sound

@Composable
fun SoundItem(
    sound: Sound,
    onPlayToggle: (Sound) -> Unit,
    onVolumeChange: (Sound, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = sound.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (sound.isPremium) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_premium),
                            contentDescription = "Premium Sound",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                IconButton(
                    onClick = { onPlayToggle(sound) }
                ) {
                    Icon(
                        imageVector = if (sound.isSelected) Icons.Filled.Build else Icons.Filled.PlayArrow,
                        contentDescription = if (sound.isSelected) "Pause ${sound.name}" else "Play ${sound.name}",
                        tint = if (sound.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Slider(
                value = sound.volume,
                onValueChange = { onVolumeChange(sound, it) },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}