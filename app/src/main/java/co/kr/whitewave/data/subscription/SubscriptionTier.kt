package co.kr.whitewave.data.subscription

sealed class SubscriptionTier {
    data object Free : SubscriptionTier()
    data object Premium : SubscriptionTier()
}