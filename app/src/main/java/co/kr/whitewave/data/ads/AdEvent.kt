package co.kr.whitewave.data.ads

sealed class AdEvent {
    data object ShowAd : AdEvent()
    data object AdClosed : AdEvent()
}