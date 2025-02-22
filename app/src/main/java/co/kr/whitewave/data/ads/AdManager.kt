package co.kr.whitewave.data.ads

import android.app.Activity
import android.content.Context
import co.kr.whitewave.data.subscription.SubscriptionManager
import co.kr.whitewave.data.subscription.SubscriptionTier
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// data/ads/AdManager.kt
class AdManager(
    private val context: Context,
    private val subscriptionManager: SubscriptionManager
) {
    companion object {
        private const val AD_UNIT_ID = "ca-app-pub-8942852781629120~7311989169" // 실제 광고 단위 ID로 교체 필요
        private const val PLAY_COUNT_THRESHOLD = 5  // 5번째마다 광고 표시
    }

    private var playCount = 0
    private var interstitialAd: InterstitialAd? = null
    private val _isLoadingAd = MutableStateFlow(false)
    val isLoadingAd: StateFlow<Boolean> = _isLoadingAd.asStateFlow()

    init {
        MobileAds.initialize(context)
        loadAd()
    }

    private fun loadAd() {
        if (interstitialAd != null || _isLoadingAd.value) return

        _isLoadingAd.value = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    _isLoadingAd.value = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    _isLoadingAd.value = false
                }
            }
        )
    }

    suspend fun shouldShowAd(): Boolean {
        if (subscriptionManager.subscriptionTier.value is SubscriptionTier.Premium) {
            return false
        }

        playCount++
        return playCount % PLAY_COUNT_THRESHOLD == 0
    }

    fun showAd(activity: Activity, onAdClosed: () -> Unit) {
        val ad = interstitialAd
        if (ad == null) {
            onAdClosed()
            loadAd()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadAd()
                onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                loadAd()
                onAdClosed()
            }
        }

        ad.show(activity)
    }
}