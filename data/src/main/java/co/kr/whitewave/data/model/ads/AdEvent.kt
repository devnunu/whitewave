package co.kr.whitewave.data.model.ads

sealed class AdEvent {
    data object ShowAd : AdEvent()
    data object AdClosed : AdEvent()
}