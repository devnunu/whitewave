package co.kr.whitewave.presentation.ui.screens.home

import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.kr.whitewave.presentation.R
import co.kr.whitewave.presentation.model.result.IntentParamKey
import co.kr.whitewave.presentation.model.result.ResultCode
import co.kr.whitewave.presentation.navigation.NavRoute
import co.kr.whitewave.presentation.ui.components.PremiumInfoDialog
import co.kr.whitewave.presentation.ui.screens.home.HomeContract.Effect
import co.kr.whitewave.presentation.ui.screens.home.HomeContract.ViewEvent
import co.kr.whitewave.presentation.ui.screens.home.components.CustomTimerDialog
import co.kr.whitewave.presentation.ui.screens.home.components.PlayingSoundsBottomSheet
import co.kr.whitewave.presentation.ui.screens.home.components.SavePresetDialog
import co.kr.whitewave.presentation.ui.screens.home.components.SoundGrid
import co.kr.whitewave.presentation.util.formatForDisplay
import co.kr.whitewave.presentation.util.navigateForResult
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

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = "WhiteWave",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
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
            // 유튜브 뮤직 스타일의 미니 플레이어
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                shape = MaterialTheme.shapes.extraSmall,
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 왼쪽: 타이머 섹션
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = { showTimerDialog = true },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_timer),
                                contentDescription = "타이머 설정",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // 타이머 표시
                        AnimatedVisibility(
                            visible = state.remainingTime != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            state.remainingTime?.let { remaining ->
                                Text(
                                    text = remaining.formatForDisplay(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // 중앙: 재생/일시정지 아이콘 버튼
                    FilledIconButton(
                        onClick = { viewModel.handleViewEvent(ViewEvent.TogglePlayback) },
                        enabled = hasPlayingSounds,
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            painter = painterResource(
                                if (state.isPlaying) R.drawable.ic_pause
                                else R.drawable.ic_play
                            ),
                            contentDescription = if (state.isPlaying) "일시정지" else "재생",
                            modifier = Modifier.size(24.dp),
                            tint = if (hasPlayingSounds)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 오른쪽: 재생 목록 섹션
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box {
                            IconButton(
                                onClick = { showPlayingSounds = true },
                                enabled = hasPlayingSounds,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_playlist),
                                    contentDescription = "재생 목록",
                                    tint = if (hasPlayingSounds)
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // 재생 중인 사운드 개수 뱃지
                            if (hasPlayingSounds) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = 4.dp)
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = playingSounds.size.toString(),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.75f
                                        )
                                    )
                                }
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
        }
    }
}
