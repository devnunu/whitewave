package co.kr.whitewave.ui.screens.preset

import androidx.lifecycle.viewModelScope
import co.kr.whitewave.data.repository.PresetLimitExceededException
import co.kr.whitewave.data.repository.PresetRepository
import co.kr.whitewave.ui.mvi.BaseViewModel
import co.kr.whitewave.ui.screens.preset.PresetContract.Effect
import co.kr.whitewave.ui.screens.preset.PresetContract.Intent
import co.kr.whitewave.ui.screens.preset.PresetContract.State
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PresetViewModel(
    private val presetRepository: PresetRepository
) : BaseViewModel<State, Intent, Effect>(State()) {

    init {
        // 프리셋 목록 로드
        handleIntent(Intent.LoadPresets)
    }

    override fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadPresets -> loadPresets()
            is Intent.SavePreset -> savePreset(intent.name, intent.sounds)
            is Intent.DeletePreset -> deletePreset(intent.presetId)
            is Intent.SelectPreset -> selectPreset(intent.preset)
        }
    }

    private fun loadPresets() {
        setState { it.copy(isLoading = true) }

        presetRepository.getAllPresets()
            .onEach { presets ->
                setState {
                    it.copy(
                        presets = presets,
                        isLoading = false,
                        error = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun savePreset(name: String, sounds: List<co.kr.whitewave.data.model.Sound>) {
        viewModelScope.launch {
            try {
                presetRepository.savePreset(name, sounds)
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

    private fun deletePreset(presetId: String) {
        viewModelScope.launch {
            try {
                presetRepository.deletePreset(presetId)
                sendEffect(Effect.ShowSnackbar("프리셋이 삭제되었습니다"))
            } catch (e: Exception) {
                setState { it.copy(error = e.message) }
                sendEffect(Effect.ShowSnackbar(e.message ?: "프리셋 삭제 오류"))
            }
        }
    }

    private fun selectPreset(preset: co.kr.whitewave.data.local.PresetWithSounds) {
        sendEffect(Effect.PresetSelected(preset))
    }
}