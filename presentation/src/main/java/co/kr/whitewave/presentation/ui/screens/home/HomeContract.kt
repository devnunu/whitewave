package co.kr.whitewave.presentation.ui.screens.home

import android.app.Activity
import co.kr.whitewave.domain.model.preset.PresetWithSounds
import co.kr.whitewave.domain.model.sound.Sound
import co.kr.whitewave.domain.model.sound.SoundCategory
import co.kr.whitewave.domain.model.subscription.SubscriptionTier
import co.kr.whitewave.presentation.ui.base.UiEffect
import co.kr.whitewave.presentation.ui.base.UiState
import co.kr.whitewave.presentation.ui.base.UiViewEvent
import kotlin.time.Duration

/**
 * HomeScreen의 MVI 계약
 */
object HomeContract {

    /**
     * HomeScreen의 UI 상태
     */
    data class State(
        val sounds: List<Sound> = emptyList(),
        val selectedCategory: SoundCategory = SoundCategory.ALL,
        val isPlaying: Boolean = false,
        val timerDuration: Duration? = null,
        val remainingTime: Duration? = null,
        val savePresetError: String? = null,
        val playError: String? = null,
        val showPremiumDialog: Boolean = false,
        val subscriptionTier: SubscriptionTier = SubscriptionTier.Free
    ) : UiState {
        // 필터링된 사운드 목록
        val filteredSounds: List<Sound>
            get() = if (selectedCategory == SoundCategory.ALL) {
                sounds
            } else {
                sounds.filter { it.category == selectedCategory }
            }
    }

    /**
     * HomeScreen에서 발생할 수 있는 사용자 의도/액션
     */
    sealed class ViewEvent : UiViewEvent {
        object LoadSounds : ViewEvent()
        data class SelectCategory(val category: SoundCategory) : ViewEvent()
        data class ToggleSound(val sound: Sound) : ViewEvent()
        data class UpdateVolume(val sound: Sound, val volume: Float) : ViewEvent()
        data class SetTimer(val duration: Duration?) : ViewEvent()
        data class SavePreset(val name: String) : ViewEvent()
        data class LoadPreset(val preset: PresetWithSounds) : ViewEvent()
        object TogglePlayback : ViewEvent()
        object NavigateToSettings : ViewEvent()
        object DismissPremiumDialog : ViewEvent()
        data class StartSubscription(val activity: Activity) : ViewEvent()
        object OnAdClosed : ViewEvent()
    }

    /**
     * HomeScreen에서 발생할 수 있는 일회성 이벤트(사이드 이펙트)
     */
    sealed class Effect : UiEffect {
        data class ShowSnackbar(val message: String) : Effect()
        object ShowAd : Effect()
        data class NavigateTo(val route: String) : Effect()
    }
}