package co.kr.whitewave.presentation.ui.screens.home

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.kr.whitewave.presentation.R
import co.kr.whitewave.presentation.ui.components.PremiumInfoDialog
import co.kr.whitewave.presentation.ui.screens.home.HomeContract.Effect
import co.kr.whitewave.presentation.ui.screens.home.HomeContract.ViewEvent
import co.kr.whitewave.presentation.ui.screens.home.components.CustomTimerDialog
import co.kr.whitewave.presentation.ui.screens.home.components.PlayingSoundsBottomSheet
import co.kr.whitewave.presentation.ui.screens.home.components.SavePresetDialog
import co.kr.whitewave.presentation.ui.screens.home.components.SoundGrid
import co.kr.whitewave.presentation.util.formatForDisplay
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToPlayingSounds: () -> Unit = {}
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

    // 다이얼로그 처리
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
        CustomTimerDialog( // TimerPickerDialog 대신 CustomTimerDialog 사용
            onDismiss = { showTimerDialog = false },
            onSetTimer = { duration ->
                viewModel.handleViewEvent(ViewEvent.SetTimer(duration))
            }
        )
    }

    if (showPlayingSounds) {
        PlayingSoundsBottomSheet(
            playingSounds = playingSounds,
            isPlaying = state.isPlaying,
            remainingTime = state.remainingTime,
            onVolumeChange = { sound, volume ->
                viewModel.handleViewEvent(ViewEvent.UpdateVolume(sound, volume))
            },
            onSoundRemove = { sound ->
                viewModel.handleViewEvent(ViewEvent.ToggleSound(sound))
            },
            onTogglePlayback = {
                viewModel.handleViewEvent(ViewEvent.TogglePlayback)
            },
            onSetTimer = { duration ->
                viewModel.handleViewEvent(ViewEvent.SetTimer(duration))
            },
            onCancelTimer = {
                viewModel.handleViewEvent(ViewEvent.SetTimer(null))
            },
            onDismiss = { showPlayingSounds = false }
        )
    }

    // Effect 처리
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

    // 에러 메시지 스낵바 표시
    LaunchedEffect(state.playError) {
        state.playError?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        androidx.compose.ui.graphics.Color(0xFF0A1929),
                        androidx.compose.ui.graphics.Color(0xFF1A2332)
                    )
                )
            )
    ) {
        androidx.compose.material3.Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 상단 타이틀
                Text(
                    text = "WhiteWave",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    textAlign = TextAlign.Center
                )

                // Sound grid
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

                // 새로운 하단 재생 컨트롤러
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = hasPlayingSounds) {
                            showPlayingSounds = true
                        },
                    shape = MaterialTheme.shapes.extraSmall,
                    color = androidx.compose.ui.graphics.Color(0xFF0F1F2E),
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 왼쪽: 재생/일시정지 버튼
                        FilledIconButton(
                            onClick = { viewModel.handleViewEvent(ViewEvent.TogglePlayback) },
                            enabled = hasPlayingSounds,
                            modifier = Modifier.size(40.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = androidx.compose.ui.graphics.Color.White,
                                disabledContainerColor = androidx.compose.ui.graphics.Color(0xFF2A3A4A)
                            )
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (state.isPlaying) R.drawable.ic_pause
                                    else R.drawable.ic_play
                                ),
                                contentDescription = if (state.isPlaying) "일시정지" else "재생",
                                modifier = Modifier.size(20.dp),
                                tint = if (hasPlayingSounds)
                                    androidx.compose.ui.graphics.Color.Black
                                else
                                    androidx.compose.ui.graphics.Color(0xFF4A5A6A)
                            )
                        }

                        // 중앙: 재생 정보 및 타이머
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            if (hasPlayingSounds) {
                                val soundNames = playingSounds.joinToString(", ") { it.name }
                                Text(
                                    text = "$soundNames playing",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = androidx.compose.ui.graphics.Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                state.remainingTime?.let { remaining ->
                                    Text(
                                        text = remaining.formatForDisplay(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = androidx.compose.ui.graphics.Color(0xFF8A9AAA),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            } else {
                                Text(
                                    text = "재생중인 음악이 없습니다",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = androidx.compose.ui.graphics.Color(0xFF4A5A6A)
                                )
                            }
                        }

                        // 오른쪽: 오디오 파형과 정지 버튼
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 오디오 파형 시각화
                            if (state.isPlaying) {
                                AudioWaveform(
                                    modifier = Modifier.size(width = 60.dp, height = 24.dp)
                                )
                            }

                            // 정지 버튼
                            IconButton(
                                onClick = {
                                    playingSounds.forEach { sound ->
                                        viewModel.handleViewEvent(ViewEvent.ToggleSound(sound))
                                    }
                                },
                                enabled = hasPlayingSounds,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_stop),
                                    contentDescription = "정지",
                                    tint = if (hasPlayingSounds)
                                        androidx.compose.ui.graphics.Color.White
                                    else
                                        androidx.compose.ui.graphics.Color(0xFF4A5A6A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioWaveform(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(12) { index ->
            val animatedHeight by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (300..600).random(),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = androidx.compose.animation.core.StartOffset(index * 50)
                ),
                label = "bar$index"
            )

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight(animatedHeight)
                    .background(
                        color = androidx.compose.ui.graphics.Color(0xFF00D9FF),
                        shape = MaterialTheme.shapes.extraSmall
                    )
            )
        }
    }
}
