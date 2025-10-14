package co.kr.whitewave.data.model.subscription

sealed class SubscriptionTierEntity {
    data object Free : SubscriptionTierEntity()
    data object Premium : SubscriptionTierEntity()
}