// data/subscription/SubscriptionManager.kt
package co.kr.whitewave.data.subscription

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "subscription")

// data/subscription/SubscriptionManager.kt
class SubscriptionManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    companion object {
        private const val SUBSCRIPTION_ID = "whitewave_premium_monthly"
        private const val SUBSCRIPTION_CHECK_INTERVAL = 3600000L // 1시간을 밀리초로 변환한 값
        private val SUBSCRIPTION_STATUS_KEY = booleanPreferencesKey("subscription_status")
    }

    private val dataStore = context.dataStore
    private val billingClient = BillingClient.newBuilder(context)
        .setListener(::onPurchasesUpdated)
        .enablePendingPurchases()
        .build()

    private val _subscriptionTier = MutableStateFlow<SubscriptionTier>(SubscriptionTier.Free)
    val subscriptionTier: StateFlow<SubscriptionTier> = _subscriptionTier.asStateFlow()

    init {
        coroutineScope.launch {
            // 캐시된 구독 상태 로드
            dataStore.data.firstOrNull()?.let { preferences ->
                if (preferences[SUBSCRIPTION_STATUS_KEY] == true) {
                    _subscriptionTier.value = SubscriptionTier.Premium
                }
            }

            // Billing Client 연결 및 실제 구독 상태 확인
            establishConnection()

            // 주기적으로 구독 상태 체크
            while (true) {
                queryPurchases()
                delay(SUBSCRIPTION_CHECK_INTERVAL)
            }
        }
    }

    private suspend fun establishConnection() {
        try {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        coroutineScope.launch {
                            queryPurchases()
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    coroutineScope.launch {
                        delay(1000) // 잠시 대기 후 재시도
                        establishConnection()
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("SubscriptionManager", "Error establishing billing connection", e)
        }
    }

    private suspend fun queryPurchases() {
        try {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            val result = billingClient.queryPurchasesAsync(params)
            val hasValidSubscription = result.purchasesList.any { purchase ->
                purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                        !purchase.isAcknowledged
            }

            updateSubscriptionStatus(hasValidSubscription)
        } catch (e: Exception) {
            Log.e("SubscriptionManager", "Error querying purchases", e)
        }
    }

    private fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            coroutineScope.launch {
                purchases?.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams)
            }
            updateSubscriptionStatus(true)
        }
    }

    private suspend fun updateSubscriptionStatus(isPremium: Boolean) {
        _subscriptionTier.value = if (isPremium) SubscriptionTier.Premium else SubscriptionTier.Free
        cacheSubscriptionStatus(isPremium)
    }

    private suspend fun cacheSubscriptionStatus(isPremium: Boolean) {
        dataStore.edit { preferences ->
            preferences[SUBSCRIPTION_STATUS_KEY] = isPremium
        }
    }

    suspend fun startSubscription(activity: Activity) {
        val productDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(SUBSCRIPTION_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(
            productDetailsParams,
            { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productDetailsList.firstOrNull()?.let { productDetails ->
                        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
                        if (offerToken != null) {
                            val billingFlowParams = BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(
                                    listOf(
                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                            .setProductDetails(productDetails)
                                            .setOfferToken(offerToken)
                                            .build()
                                    )
                                )
                                .build()
                            billingClient.launchBillingFlow(activity, billingFlowParams)
                        }
                    }
                }
            }
        )
    }

    fun release() {
        billingClient.endConnection()
    }
}