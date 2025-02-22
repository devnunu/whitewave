package co.kr.whitewave.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.kr.whitewave.R
import co.kr.whitewave.ui.components.SavePresetDialog
import co.kr.whitewave.ui.components.SoundItem
import co.kr.whitewave.ui.components.TimerPicker
import co.kr.whitewave.utils.formatForDisplay
import org.koin.androidx.compose.koinViewModel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

val md_theme_light_primary = Color(0xFF006C4C)
val md_theme_light_background = Color(0xFFFBFDF8)
val md_theme_light_surface = Color(0xFFFBFDF8)

val md_theme_dark_primary = Color(0xFF67DBB3)
val md_theme_dark_background = Color(0xFF191C1A)
val md_theme_dark_surface = Color(0xFF191C1A)

// ui/screens/home/HomeScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onPresetClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    var showSavePresetDialog by remember { mutableStateOf(false) }
    val sounds by viewModel.sounds.collectAsState()
    val timerDuration by viewModel.timerDuration.collectAsState()
    val remainingTime by viewModel.remainingTime.collectAsState()
    val savePresetError by viewModel.savePresetError.collectAsState()

    if (showSavePresetDialog) {
        SavePresetDialog(
            onDismiss = { showSavePresetDialog = false },
            onSave = {
                viewModel.savePreset(it)
                showSavePresetDialog = false
            },
            error = savePresetError
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WhiteWave") },
                actions = {
                    IconButton(onClick = onPresetClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_preset),
                            contentDescription = "프리셋"
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "설정"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Timer section with card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TimerPicker(
                        duration = timerDuration,
                        onDurationSelect = viewModel::setTimer
                    )
                    remainingTime?.let { remaining ->
                        Text(
                            text = "Remaining: ${remaining.formatForDisplay()}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Sound list
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sounds) { sound ->
                    SoundItem(
                        sound = sound,
                        onPlayToggle = viewModel::toggleSound,
                        onVolumeChange = viewModel::updateVolume
                    )
                }
            }

            // Save preset button
            Button(
                onClick = { showSavePresetDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Save Current Mix")
            }
        }
    }

    if (showSavePresetDialog) {
        SavePresetDialog(
            onDismiss = { showSavePresetDialog = false },
            onSave = { name ->
                viewModel.savePreset(name)
            }
        )
    }
}