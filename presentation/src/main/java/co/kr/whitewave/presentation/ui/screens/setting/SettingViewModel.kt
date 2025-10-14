package co.kr.whitewave.presentation.ui.screens.setting

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import co.kr.whitewave.data.manager.SubscriptionManager
import co.kr.whitewave.presentation.base.BaseViewModel
import co.kr.whitewave.presentation.ui.screens.setting.SettingContract.Effect
import co.kr.whitewave.presentation.ui.screens.setting.SettingContract.State
import co.kr.whitewave.presentation.ui.screens.setting.SettingContract.ViewEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val subscriptionManager: SubscriptionManager
) : BaseViewModel<State, ViewEvent, Effect>(State()) {

    init {
        // 구독 상태 모니터링
        subscriptionManager.subscriptionTier
            .onEach { tier ->
                setState { it.copy(subscriptionTier = tier) }
            }
            .launchIn(viewModelScope)
    }

    override fun handleViewEvent(viewEvent: ViewEvent) {
        when (viewEvent) {
            is ViewEvent.StartSubscription -> startSubscription(viewEvent.activity)
            is ViewEvent.CheckNotificationPermission -> checkNotificationPermission(viewEvent.activity)
            is ViewEvent.OpenNotificationSettings -> sendEffect(Effect.NavigateToNotificationSettings)
            is ViewEvent.ShowPremiumInfo -> sendEffect(Effect.ShowPremiumDialog)
            is ViewEvent.NavigateBack -> sendEffect(Effect.NavigateBack)
        }
    }

    private fun startSubscription(activity: Activity) {
        viewModelScope.launch {
            try {
                subscriptionManager.startSubscription(activity)
                sendEffect(Effect.ShowSnackbar("구독 프로세스가 시작되었습니다"))
            } catch (e: Exception) {
                sendEffect(Effect.ShowSnackbar("구독 시작 중 오류가 발생했습니다: ${e.message}"))
            }
        }
    }

    private fun checkNotificationPermission(context: Context) {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Android 13 미만은 런타임 권한 필요 없음
        }

        setState { it.copy(hasNotificationPermission = hasPermission) }
    }
}