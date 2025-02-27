package co.kr.whitewave.ui.screens.preset

import co.kr.whitewave.data.local.PresetWithSounds
import co.kr.whitewave.data.model.Sound
import co.kr.whitewave.data.subscription.SubscriptionTier
import co.kr.whitewave.ui.mvi.UiEffect
import co.kr.whitewave.ui.mvi.UiViewEvent
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
        val categories: List<String> = emptyList(),
        val selectedCategory: String = "모두",
        val isLoading: Boolean = false,
        val error: String? = null,
        val editMode: Boolean = false,
        val currentEditPreset: PresetWithSounds? = null,
        val showPremiumDialog: Boolean = false,
        val subscriptionTier: SubscriptionTier = SubscriptionTier.Free
    ) : UiState

    /**
     * PresetScreen에서 발생할 수 있는 사용자 의도/액션
     */
    sealed class ViewEvent : UiViewEvent {
        object LoadPresets : ViewEvent()
        data class SelectCategory(val category: String) : ViewEvent()
        data class SavePreset(val name: String, val sounds: List<Sound>, val category: String) : ViewEvent()
        data class UpdatePreset(
            val presetId: String,
            val name: String,
            val sounds: List<Sound>,
            val category: String
        ) : ViewEvent()
        data class DeletePreset(val presetId: String) : ViewEvent()
        data class SelectPreset(val preset: PresetWithSounds) : ViewEvent()
        data class StartEditPreset(val preset: PresetWithSounds) : ViewEvent()
        object CancelEditPreset : ViewEvent()
        data class ShowSnackbarMessage(val message: String) : ViewEvent()
        data class ShowDialog(val title: String, val message: String) : ViewEvent()
        // 프리미엄 관련 이벤트
        object ShowPremiumDialog : ViewEvent()
        object DismissPremiumDialog : ViewEvent()
        data class StartSubscription(val activity: android.app.Activity) : ViewEvent()
    }

    /**
     * PresetScreen에서 발생할 수 있는 일회성 이벤트(사이드 이펙트)
     */
    sealed class Effect : UiEffect {
        data class ShowSnackbar(val message: String) : Effect()
        // 프리셋 자체가 아닌 ID만 전달하도록 변경
        data class PresetSelected(val presetId: String) : Effect()
        object NavigateBack : Effect()
        data class ShowDialog(val title: String, val message: String) : Effect()
        // 프리미엄 관련 이펙트
        object ShowPremiumDialog : Effect()
    }
}