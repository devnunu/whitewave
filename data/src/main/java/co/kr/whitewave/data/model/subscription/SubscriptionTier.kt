package co.kr.whitewave.data.model.subscription

sealed class SubscriptionTier {
    data object Free : SubscriptionTier()
    data object Premium : SubscriptionTier()
}