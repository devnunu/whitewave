package co.kr.whitewave.domain.model.subscription


sealed class SubscriptionTier {
    data object Free : SubscriptionTier()
    data object Premium : SubscriptionTier()
}