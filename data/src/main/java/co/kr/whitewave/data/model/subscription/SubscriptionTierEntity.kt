package co.kr.whitewave.data.model.subscription

import co.kr.whitewave.data.DataMapper
import co.kr.whitewave.domain.model.subscription.SubscriptionTier

sealed class SubscriptionTierEntity: DataMapper<SubscriptionTier> {
    data object Free : SubscriptionTierEntity() {
        override fun toDomain(): SubscriptionTier {
            TODO("Not yet implemented")
        }
    }
    data object Premium : SubscriptionTierEntity() {
        override fun toDomain(): SubscriptionTier {
            TODO("Not yet implemented")
        }
    }
}