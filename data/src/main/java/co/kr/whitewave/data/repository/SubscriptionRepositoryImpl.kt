package co.kr.whitewave.data.repository

import android.app.Activity
import co.kr.whitewave.data.local.SubscriptionLocalDataSource
import co.kr.whitewave.domain.model.subscription.SubscriptionTier
import co.kr.whitewave.domain.repository.SubscriptionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SubscriptionRepositoryImpl(
    private val subscriptionLocalDataSource: SubscriptionLocalDataSource
) : SubscriptionRepository {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override val subscriptionTier: StateFlow<SubscriptionTier> =
        subscriptionLocalDataSource.subscriptionTier
            .map { it.toDomain() }
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = SubscriptionTier.Free
            )

    override suspend fun startSubscription(activity: Activity) {
        subscriptionLocalDataSource.startSubscription(activity)
    }

    override fun release() {
        subscriptionLocalDataSource.release()
    }
}
