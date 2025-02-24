package co.kr.whitewave.ui.screens.preset

import co.kr.whitewave.data.local.PresetWithSounds
import co.kr.whitewave.data.model.Sound
import co.kr.whitewave.ui.mvi.UiEffect
import co.kr.whitewave.ui.mvi.UiIntent
import co.kr.whitewave.ui.mvi.UiState

/**
 * PresetScreen의 MVI 계약
 */
object PresetContract {

    /**
     * PresetScreen의 UI 상태
     */
    data class State(
        val presets: List<PresetWithSounds> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    /**
     * PresetScreen에서 발생할 수 있는 사용자 의도/액션
     */
    sealed class Intent : UiIntent {
        object LoadPresets : Intent()
        data class SavePreset(val name: String, val sounds: List<Sound>) : Intent()
        data class DeletePreset(val presetId: String) : Intent()
        data class SelectPreset(val preset: PresetWithSounds) : Intent()
    }

    /**
     * PresetScreen에서 발생할 수 있는 일회성 이벤트(사이드 이펙트)
     */
    sealed class Effect : UiEffect {
        data class ShowSnackbar(val message: String) : Effect()
        data class PresetSelected(val preset: PresetWithSounds) : Effect()
        object NavigateBack : Effect()
    }
}