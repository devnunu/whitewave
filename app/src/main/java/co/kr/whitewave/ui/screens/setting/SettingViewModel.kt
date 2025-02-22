package co.kr.whitewave.ui.screens.setting

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.kr.whitewave.data.subscription.SubscriptionManager
import co.kr.whitewave.data.subscription.SubscriptionTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val subscriptionManager: SubscriptionManager
) : ViewModel() {
    val subscriptionTier = subscriptionManager.subscriptionTier
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SubscriptionTier.Free
        )

    fun startSubscription(activity: Activity) {
        viewModelScope.launch {
            subscriptionManager.startSubscription(activity)
        }
    }
}