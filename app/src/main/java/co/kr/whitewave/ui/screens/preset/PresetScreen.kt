package co.kr.whitewave.ui.screens.preset

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.navigation.NavController
import co.kr.whitewave.data.model.DefaultSounds
import co.kr.whitewave.data.model.result.IntentParamKey.PRESET_ID
import co.kr.whitewave.data.model.result.ResultCode
import co.kr.whitewave.ui.screens.preset.components.CategoryTabRow
import co.kr.whitewave.ui.screens.preset.components.PresetCard
import co.kr.whitewave.ui.screens.preset.components.PresetEditDialog
import co.kr.whitewave.ui.screens.preset.PresetContract.Effect
import co.kr.whitewave.ui.screens.preset.PresetContract.ViewEvent
import co.kr.whitewave.utils.popBackStackWithResult
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetScreen(
    viewModel: PresetViewModel = koinViewModel(),
    navController: NavController,
    onBackClick: () -> Unit = {}
) {
    // MVI State 수집
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 다이얼로그 상태
    var showAlertDialog by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }

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

                is Effect.PresetSelected -> {
                    val intent = Intent().apply { putExtra(PRESET_ID, effect.presetId) }
                    val result = ActivityResult(ResultCode.SUCCESS, intent)
                    navController.popBackStackWithResult(result)
                }

                is Effect.NavigateBack -> {
                    onBackClick()
                }

                is Effect.ShowDialog -> {
                    alertTitle = effect.title
                    alertMessage = effect.message
                    showAlertDialog = true
                }
            }
        }
    }

    // 알림 다이얼로그
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = { Text(alertTitle) },
            text = { Text(alertMessage) },
            confirmButton = {
                TextButton(onClick = { showAlertDialog = false }) {
                    Text("확인")
                }
            }
        )
    }

    // 프리셋 편집 다이얼로그
    if (state.editMode && state.currentEditPreset != null) {
        PresetEditDialog(
            preset = state.currentEditPreset,
            availableSounds = DefaultSounds.ALL,
            onSave = { name, sounds, category ->
                viewModel.handleViewEvent(
                    ViewEvent.UpdatePreset(
                        presetId = state.currentEditPreset!!.preset.id,
                        name = name,
                        sounds = sounds,
                        category = category
                    )
                )
            },
            onDismiss = {
                viewModel.handleViewEvent(ViewEvent.CancelEditPreset)
            },
            error = state.error
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("프리셋") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "뒤로가기"
                            )
                        }
                    }
                )

                // 카테고리 탭 (기존 코드)
                CategoryTabRow(
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = { category ->
                        viewModel.handleViewEvent(ViewEvent.SelectCategory(category))
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.presets.isEmpty() -> {
                    Text(
                        text = if (state.selectedCategory == "커스텀")
                            "저장된 커스텀 프리셋이 없습니다"
                        else
                            "해당 카테고리의 프리셋이 없습니다",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.presets) { preset ->
                            PresetCard(
                                preset = preset,
                                onClick = {
                                    viewModel.handleViewEvent(ViewEvent.SelectPreset(preset))
                                },
                                onEdit = {
                                    viewModel.handleViewEvent(ViewEvent.StartEditPreset(preset))
                                },
                                onDelete = {
                                    viewModel.handleViewEvent(ViewEvent.DeletePreset(preset.preset.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}