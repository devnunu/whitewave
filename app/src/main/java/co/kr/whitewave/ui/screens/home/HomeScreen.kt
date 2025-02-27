package co.kr.whitewave.ui.screens.home

import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.kr.whitewave.R
import co.kr.whitewave.data.model.result.IntentParamKey
import co.kr.whitewave.data.model.result.ResultCode
import co.kr.whitewave.ui.components.PremiumInfoDialog
import co.kr.whitewave.ui.navigation.NavRoute
import co.kr.whitewave.ui.screens.home.components.SavePresetDialog
import co.kr.whitewave.ui.screens.home.components.SoundGrid
import co.kr.whitewave.ui.screens.home.HomeContract.Effect
import co.kr.whitewave.ui.screens.home.HomeContract.ViewEvent
import co.kr.whitewave.ui.screens.home.components.PlayingSoundsBottomSheet
import co.kr.whitewave.ui.screens.home.components.TimerPickerDialog
import co.kr.whitewave.utils.formatForDisplay
import co.kr.whitewave.utils.navigateForResult
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // MVI State 수집
    val state by viewModel.state.collectAsState()

    // UI 상태 변수
    var showSavePresetDialog by remember { mutableStateOf(false) }
    var showTimerDialog by remember { mutableStateOf(false) }
    var showPlayingSounds by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // 필터링된 데이터
    val playingSounds = state.sounds.filter { it.isSelected }
    val hasPlayingSounds = playingSounds.isNotEmpty()

    // 다이얼로그 처리 (기존 코드)
    if (showSavePresetDialog) {
        SavePresetDialog(
            onDismiss = { showSavePresetDialog = false },
            onSave = { name ->
                viewModel.handleViewEvent(ViewEvent.SavePreset(name))
                showSavePresetDialog = false
            },
            error = state.savePresetError
        )
    }

    if (state.showPremiumDialog) {
        PremiumInfoDialog(
            onDismiss = { viewModel.handleViewEvent(ViewEvent.DismissPremiumDialog) },
            onSubscribe = {
                activity?.let {
                    viewModel.handleViewEvent(ViewEvent.StartSubscription(it))
                }
            }
        )
    }

    if (showTimerDialog) {
        TimerPickerDialog(
            selectedDuration = state.timerDuration,
            onDurationSelect = { duration ->
                viewModel.handleViewEvent(ViewEvent.SetTimer(duration))
                showTimerDialog = false
            },
            onDismiss = { showTimerDialog = false }
        )
    }

    if (showPlayingSounds) {
        PlayingSoundsBottomSheet(
            playingSounds = playingSounds,
            onVolumeChange = { sound, volume ->
                viewModel.handleViewEvent(ViewEvent.UpdateVolume(sound, volume))
            },
            onSoundRemove = { sound ->
                viewModel.handleViewEvent(ViewEvent.ToggleSound(sound))
            },
            onSavePreset = { showSavePresetDialog = true },
            onDismiss = { showPlayingSounds = false }
        )
    }

    // Effect 처리 (기존 코드)
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is Effect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }

                is Effect.ShowAd -> {
                    activity?.let { viewModel.onAdClosed() }
                }

                is Effect.NavigateTo -> {
                    // 네비게이션 처리 (필요시)
                }
            }
        }
    }

    // 에러 메시지 스낵바 표시 (기존 코드)
    LaunchedEffect(state.playError) {
        state.playError?.let {
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
                    // 프리셋 아이콘
                    IconButton(onClick = {
                        navController.navigateForResult<ActivityResult?>(
                            route = NavRoute.Presets,
                            navResultCallback = { result ->
                                if (result?.resultCode == ResultCode.SUCCESS) {
                                    val presetId =
                                        result.data?.getStringExtra(IntentParamKey.PRESET_ID)
                                            .orEmpty()
                                    viewModel.loadPresetById(presetId)
                                }
                            }
                        )
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_preset),
                            contentDescription = "프리셋"
                        )
                    }

                    // 설정 아이콘
                    IconButton(onClick = {
                        navController.navigate(NavRoute.Settings)
                    }) {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timer section (기존 코드)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = { showTimerDialog = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_timer),
                                contentDescription = "Timer"
                            )
                        }
                        state.remainingTime?.let { remaining ->
                            Text(
                                text = remaining.formatForDisplay(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    // Play/Pause button (기존 코드)
                    Button(
                        onClick = { viewModel.handleViewEvent(ViewEvent.TogglePlayback) },
                        enabled = hasPlayingSounds
                    ) {
                        Icon(
                            painter = painterResource(
                                if (state.isPlaying) R.drawable.ic_pause
                                else R.drawable.ic_play
                            ),
                            contentDescription = if (state.isPlaying) "Pause" else "Play"
                        )
                        Text(
                            text = if (state.isPlaying) "정지" else "재생",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    // Playing sounds button (기존 코드)
                    Box(
                        modifier = Modifier.padding(start = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { showPlayingSounds = true },
                            enabled = hasPlayingSounds
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_music_note),
                                contentDescription = "Playing sounds",
                                tint = if (hasPlayingSounds)
                                    LocalContentColor.current
                                else
                                    LocalContentColor.current.copy(alpha = 0.38f)
                            )
                        }
                        if (hasPlayingSounds) {
                            Badge(
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Text(playingSounds.size.toString())
                            }
                        }
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
            // Sound grid (기존 코드)
            SoundGrid(
                sounds = state.sounds,
                onSoundSelect = { sound ->
                    viewModel.handleViewEvent(ViewEvent.ToggleSound(sound))
                },
                onVolumeChange = { sound, volume ->
                    viewModel.handleViewEvent(ViewEvent.UpdateVolume(sound, volume))
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}