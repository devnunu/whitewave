package co.kr.whitewave.ui.screens.setting

import android.app.Activity
import co.kr.whitewave.data.subscription.SubscriptionTier
import co.kr.whitewave.ui.mvi.UiEffect
import co.kr.whitewave.ui.mvi.UiViewEvent
import co.kr.whitewave.ui.mvi.UiState

/**
 * SettingScreen의 MVI 계약
 */
object SettingContract {

    /**
     * SettingScreen의 UI 상태
     */
    data class State(
        val subscriptionTier: SubscriptionTier = SubscriptionTier.Free,
        val hasNotificationPermission: Boolean = false
    ) : UiState

    /**
     * SettingScreen에서 발생할 수 있는 사용자 의도/액션
     */
    sealed class ViewEvent : UiViewEvent {
        data class StartSubscription(val activity: Activity) : ViewEvent()
        data class CheckNotificationPermission(val activity: Activity) : ViewEvent()
        object OpenNotificationSettings : ViewEvent()
        object ShowPremiumInfo : ViewEvent()
        object NavigateBack : ViewEvent()
    }

    /**
     * SettingScreen에서 발생할 수 있는 일회성 이벤트(사이드 이펙트)
     */
    sealed class Effect : UiEffect {
        object NavigateToNotificationSettings : Effect()
        object ShowPremiumDialog : Effect()
        object NavigateBack : Effect()
        data class ShowSnackbar(val message: String) : Effect()
    }
}