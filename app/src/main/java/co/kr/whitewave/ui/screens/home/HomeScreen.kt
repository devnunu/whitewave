package co.kr.whitewave.ui.screens.home

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.kr.whitewave.R
import co.kr.whitewave.ui.components.SavePresetDialog
import co.kr.whitewave.ui.components.SoundItem
import co.kr.whitewave.ui.components.TimerPicker
import co.kr.whitewave.utils.formatForDisplay
import org.koin.androidx.compose.koinViewModel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import co.kr.whitewave.data.ads.AdEvent
import co.kr.whitewave.data.ads.AdManager
import co.kr.whitewave.ui.components.PremiumInfoDialog
import co.kr.whitewave.ui.components.SoundGrid
import co.kr.whitewave.ui.components.TimerPickerDialog
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    adManager: AdManager = koinInject(),
    onPresetClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val showPremiumDialog by viewModel.showPremiumDialog.collectAsState()
    var showSavePresetDialog by remember { mutableStateOf(false) }
    var showTimerDialog by remember { mutableStateOf(false) }
    val sounds by viewModel.sounds.collectAsState()
    val timerDuration by viewModel.timerDuration.collectAsState()
    val remainingTime by viewModel.remainingTime.collectAsState()
    val savePresetError by viewModel.savePresetError.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    val playError by viewModel.playError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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

    if (showPremiumDialog) {
        PremiumInfoDialog(
            onDismiss = viewModel::dismissPremiumDialog,
            onSubscribe = {
                viewModel.startSubscription(context as Activity)
                viewModel.dismissPremiumDialog()
            }
        )
    }

    if (showTimerDialog) {
        TimerPickerDialog(
            selectedDuration = timerDuration,
            onDurationSelect = { duration ->
                viewModel.setTimer(duration)
                showTimerDialog = false
            },
            onDismiss = { showTimerDialog = false }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.adEvent.collect { event ->
            when (event) {
                is AdEvent.ShowAd -> {
                    activity?.let {
                        adManager.showAd(it) {
                            viewModel.onAdClosed()
                        }
                    }
                }

                else -> Unit
            }
        }
    }

    LaunchedEffect(playError) {
        playError?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
        }
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Save preset button
                Button(
                    onClick = { showSavePresetDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Save Current Mix")
                }

                // Control bar with Timer and Play button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = { showTimerDialog = true }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_timer),
                                contentDescription = "Timer"
                            )
                        }
                        remainingTime?.let { remaining ->
                            Text(
                                text = remaining.formatForDisplay(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.togglePlayback() }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isPlaying) R.drawable.ic_pause
                                else R.drawable.ic_play,
                            ),
                            contentDescription = if (isPlaying) "Pause" else "Play"
                        )
                        Text(
                            text = if (isPlaying) "정지" else "재생",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sound grid
            SoundGrid(
                sounds = sounds,
                onSoundSelect = viewModel::toggleSound,
                onVolumeChange = viewModel::updateVolume,
                modifier = Modifier.weight(1f)
            )
        }
    }
}