package co.kr.whitewave.presentation.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import co.kr.whitewave.domain.model.subscription.SubscriptionTier
import co.kr.whitewave.domain.repository.SubscriptionRepository
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

class AdManager(
    private val context: Context,
    private val subscriptionRepository: SubscriptionRepository
) {
    companion object {
        // 전면 광고용 테스트 ID
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        private const val PLAY_COUNT_THRESHOLD = 3  // 3번째마다 광고 표시
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
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d("AdManager", "Ad loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    Log.e("AdManager", "Ad failed to load: ${error.message}")
                    // 실패 시 재시도
                    loadAd()
                }
            }
        )
    }

    fun shouldShowAd(): Boolean {
        if (subscriptionRepository.subscriptionTier.value is SubscriptionTier.Premium) {
            return false
        }

        playCount++
        return playCount % PLAY_COUNT_THRESHOLD == 0
    }

    fun showAd(activity: Activity, onAdClosed: () -> Unit) {
        val ad = interstitialAd
        if (ad == null) {
            onAdClosed()
            loadAd()  // 다음 광고를 미리 로드
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadAd()  // 다음 광고를 미리 로드
                onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                loadAd()  // 다음 광고를 미리 로드
                onAdClosed()
            }
        }

        ad.show(activity)
    }
}