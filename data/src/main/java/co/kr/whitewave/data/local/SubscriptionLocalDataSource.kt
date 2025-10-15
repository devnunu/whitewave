package co.kr.whitewave.data.local

import android.app.Activity
import co.kr.whitewave.data.model.subscription.SubscriptionTierEntity
import kotlinx.coroutines.flow.StateFlow

interface SubscriptionLocalDataSource {

    val subscriptionTier: StateFlow<SubscriptionTierEntity>

    suspend fun startSubscription(activity: Activity)

    fun release()
}
