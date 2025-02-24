package co.kr.whitewave.ui.screens.home

import android.app.Activity
import co.kr.whitewave.data.local.PresetWithSounds
import co.kr.whitewave.data.model.Sound
import co.kr.whitewave.data.subscription.SubscriptionTier
import co.kr.whitewave.ui.mvi.UiEffect
import co.kr.whitewave.ui.mvi.UiIntent
import co.kr.whitewave.ui.mvi.UiState
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
        val isPlaying: Boolean = false,
        val timerDuration: Duration? = null,
        val remainingTime: Duration? = null,
        val savePresetError: String? = null,
        val playError: String? = null,
        val showPremiumDialog: Boolean = false,
        val subscriptionTier: SubscriptionTier = SubscriptionTier.Free
    ) : UiState

    /**
     * HomeScreen에서 발생할 수 있는 사용자 의도/액션
     */
    sealed class Intent : UiIntent {
        object LoadSounds : Intent()
        data class ToggleSound(val sound: Sound) : Intent()
        data class UpdateVolume(val sound: Sound, val volume: Float) : Intent()
        data class SetTimer(val duration: Duration?) : Intent()
        data class SavePreset(val name: String) : Intent()
        data class LoadPreset(val preset: PresetWithSounds) : Intent()
        object TogglePlayback : Intent()
        object DismissPremiumDialog : Intent()
        data class StartSubscription(val activity: Activity) : Intent()
        object OnAdClosed : Intent()
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