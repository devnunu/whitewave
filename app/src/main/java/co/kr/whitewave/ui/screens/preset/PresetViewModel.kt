package co.kr.whitewave.ui.screens.preset

import androidx.lifecycle.viewModelScope
import co.kr.whitewave.data.model.PresetCategories
import co.kr.whitewave.data.repository.DefaultPresetDeletionException
import co.kr.whitewave.data.repository.PresetLimitExceededException
import co.kr.whitewave.data.repository.PresetRepository
import co.kr.whitewave.ui.mvi.BaseViewModel
import co.kr.whitewave.ui.screens.preset.PresetContract.Effect
import co.kr.whitewave.ui.screens.preset.PresetContract.ViewEvent
import co.kr.whitewave.ui.screens.preset.PresetContract.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class PresetViewModel(
    private val presetRepository: PresetRepository
) : BaseViewModel<State, ViewEvent, Effect>(
    State(categories = PresetCategories.LIST)
) {
    private val selectedCategoryFlow = MutableStateFlow(PresetCategories.ALL)

    init {
        // 카테고리 선택에 따라 프리셋 목록 필터링
        combine(
            presetRepository.getAllPresets(),
            selectedCategoryFlow
        ) { allPresets, selectedCategory ->
            setState { state ->
                // 사용자 커스텀 프리셋과 기본 프리셋 분리
                val customPresets = allPresets.filter { !it.preset.isDefault }
                val defaultPresets = allPresets.filter { it.preset.isDefault }

                // 선택된 카테고리에 따라 필터링
                val filteredPresets = when (selectedCategory) {
                    PresetCategories.ALL -> {
                        // 모든 프리셋: 커스텀 프리셋을 앞에 배치하고 최신순 정렬, 기본 프리셋은 뒤에 배치
                        customPresets + defaultPresets
                    }
                    PresetCategories.CUSTOM -> {
                        // 커스텀 프리셋만: 최신순 정렬
                        customPresets
                    }
                    else -> {
                        // 특정 카테고리: 해당 카테고리 내에서 커스텀 프리셋을 앞에 배치하고, 기본 프리셋은 뒤에 배치
                        val filteredCustom = customPresets.filter { it.preset.category == selectedCategory }
                        val filteredDefault = defaultPresets.filter { it.preset.category == selectedCategory }
                        filteredCustom + filteredDefault
                    }
                }

                state.copy(
                    presets = filteredPresets,
                    selectedCategory = selectedCategory,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)

        // 초기 프리셋 로드
        handleViewEvent(ViewEvent.LoadPresets)
    }

    override fun handleViewEvent(viewEvent: ViewEvent) {
        when (viewEvent) {
            is ViewEvent.LoadPresets -> loadPresets()
            is ViewEvent.SelectCategory -> selectCategory(viewEvent.category)
            is ViewEvent.SavePreset -> savePreset(viewEvent.name, viewEvent.sounds, viewEvent.category)
            is ViewEvent.UpdatePreset -> updatePreset(
                viewEvent.presetId,
                viewEvent.name,
                viewEvent.sounds,
                viewEvent.category
            )
            is ViewEvent.DeletePreset -> deletePreset(viewEvent.presetId)
            is ViewEvent.SelectPreset -> selectPreset(viewEvent.preset)
            is ViewEvent.StartEditPreset -> startEditPreset(viewEvent.preset)
            is ViewEvent.CancelEditPreset -> cancelEditPreset()
        }
    }

    private fun loadPresets() {
        setState { it.copy(isLoading = true) }
        // 프리셋은 이미 combine 블록에서 로딩되므로 여기서는 추가 작업 필요 없음
    }

    private fun selectCategory(category: String) {
        selectedCategoryFlow.value = category
    }

    private fun savePreset(name: String, sounds: List<co.kr.whitewave.data.model.Sound>, category: String) {
        viewModelScope.launch {
            try {
                presetRepository.savePreset(name, sounds, category)
                sendEffect(Effect.ShowSnackbar("프리셋이 저장되었습니다"))
            } catch (e: PresetLimitExceededException) {
                setState { it.copy(error = e.message) }
                sendEffect(Effect.ShowSnackbar(e.message ?: "프리셋 저장 오류"))
            } catch (e: Exception) {
                setState { it.copy(error = e.message) }
                sendEffect(Effect.ShowSnackbar(e.message ?: "알 수 없는 오류가 발생했습니다"))
            }
        }
    }

    private fun updatePreset(
        presetId: String,
        name: String,
        sounds: List<co.kr.whitewave.data.model.Sound>,
        category: String
    ) {
        viewModelScope.launch {
            try {
                presetRepository.updatePreset(presetId, name, sounds, category)
                setState { it.copy(editMode = false, currentEditPreset = null) }
                sendEffect(Effect.ShowSnackbar("프리셋이 업데이트되었습니다"))
            } catch (e: Exception) {
                setState { it.copy(error = e.message) }
                sendEffect(Effect.ShowSnackbar(e.message ?: "프리셋 업데이트 오류"))
            }
        }
    }

    private fun deletePreset(presetId: String) {
        viewModelScope.launch {
            try {
                presetRepository.deletePreset(presetId)
                sendEffect(Effect.ShowSnackbar("프리셋이 삭제되었습니다"))
            } catch (e: DefaultPresetDeletionException) {
                sendEffect(Effect.ShowDialog(
                    "삭제 불가",
                    "기본 프리셋은 삭제할 수 없습니다."
                ))
            } catch (e: Exception) {
                setState { it.copy(error = e.message) }
                sendEffect(Effect.ShowSnackbar(e.message ?: "프리셋 삭제 오류"))
            }
        }
    }

    private fun selectPreset(preset: co.kr.whitewave.data.local.PresetWithSounds) {
        // 프리셋 객체 대신 ID만 전달
        sendEffect(Effect.PresetSelected(preset.preset.id))
    }

    private fun startEditPreset(preset: co.kr.whitewave.data.local.PresetWithSounds) {
        // 기본 프리셋은 편집 불가
        if (preset.preset.isDefault) {
            sendEffect(Effect.ShowDialog(
                "편집 불가",
                "기본 프리셋은 편집할 수 없습니다."
            ))
            return
        }

        setState { it.copy(editMode = true, currentEditPreset = preset) }
    }

    private fun cancelEditPreset() {
        setState { it.copy(editMode = false, currentEditPreset = null) }
    }
}