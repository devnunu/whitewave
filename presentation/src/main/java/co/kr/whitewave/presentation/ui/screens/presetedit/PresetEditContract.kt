package co.kr.whitewave.presentation.ui.screens.presetedit

import co.kr.whitewave.data.model.preset.PresetWithSoundsEntity
import co.kr.whitewave.data.model.sound.SoundEntity
import co.kr.whitewave.presentation.ui.base.UiEffect
import co.kr.whitewave.presentation.ui.base.UiState
import co.kr.whitewave.presentation.ui.base.UiViewEvent

/**
 * PresetEditScreen의 MVI 계약
 */
object PresetEditContract {

    /**
     * PresetEditScreen의 UI 상태
     */
    data class State(
        val presetId: String = "",
        val presetName: String = "",
        val sounds: List<SoundEntity> = emptyList(),
        val category: String = "",
        val isLoading: Boolean = true,
        val error: String? = null,
        val originalPreset: PresetWithSoundsEntity? = null
    ) : UiState

    /**
     * PresetEditScreen에서 발생할 수 있는 사용자 의도/액션
     */
    sealed class ViewEvent : UiViewEvent {
        data class LoadPreset(val presetId: String) : ViewEvent()
        data class ToggleSound(val sound: SoundEntity) : ViewEvent()
        data class UpdateVolume(val sound: SoundEntity, val volume: Float) : ViewEvent()
        object SavePreset : ViewEvent()
        object NavigateBack : ViewEvent()
    }

    /**
     * PresetEditScreen에서 발생할 수 있는 일회성 이벤트(사이드 이펙트)
     */
    sealed class Effect : UiEffect {
        data class ShowSnackbar(val message: String) : Effect()
        object NavigateBack : Effect()
        data class PresetSaved(val message: String) : Effect() // 메시지 추가
    }
}