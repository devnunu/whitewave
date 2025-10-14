package co.kr.whitewave.presentation.ui.screens.presetedit

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.kr.whitewave.data.model.result.IntentParamKey.MESSAGE
import co.kr.whitewave.data.model.result.IntentParamKey.PRESET_ID
import co.kr.whitewave.data.model.result.ResultCode
import co.kr.whitewave.presentation.ui.screens.home.components.SoundGrid
import co.kr.whitewave.presentation.ui.screens.presetedit.PresetEditContract.Effect
import co.kr.whitewave.presentation.ui.screens.presetedit.PresetEditContract.ViewEvent
import co.kr.whitewave.presentation.util.popBackStackWithResult
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetEditScreen(
    modifier: Modifier = Modifier,
    viewModel: PresetEditViewModel = koinViewModel(),
    navController: NavController,
    presetId: String,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // MVI State 수집
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 프리셋 로드
    LaunchedEffect(presetId) {
        viewModel.handleViewEvent(ViewEvent.LoadPreset(presetId))
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
                is Effect.NavigateBack -> {
                    onBackClick()
                }
                is Effect.PresetSaved -> {
                    // 결과 데이터와 함께 메시지 전달
                    val intent = Intent().apply {
                        putExtra(PRESET_ID, state.presetId)
                        putExtra(MESSAGE, effect.message)
                    }
                    val result = ActivityResult(ResultCode.SUCCESS, intent)
                    navController.popBackStackWithResult(result)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.presetName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding() // 물리 네비게이션 바 침범 방지
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { viewModel.handleViewEvent(ViewEvent.SavePreset) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("저장하기")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                // 로딩 표시
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("프리셋 로딩 중...")
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 프리셋 관련 정보
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "프리셋: ${state.presetName}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // 사운드 그리드
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
    }
}
