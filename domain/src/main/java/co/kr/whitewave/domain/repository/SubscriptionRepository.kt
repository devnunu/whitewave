package co.kr.whitewave.domain.repository

import android.app.Activity
import co.kr.whitewave.domain.model.subscription.SubscriptionTier
import kotlinx.coroutines.flow.StateFlow

interface SubscriptionRepository {

    val subscriptionTier: StateFlow<SubscriptionTier>

    suspend fun startSubscription(activity: Activity)

    fun release()
}
