// data/subscription/SubscriptionManager.kt
package co.kr.whitewave.data.subscription

import android.app.Activity
import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubscriptionManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    companion object {
        private const val SUBSCRIPTION_ID = "whitewave_premium_monthly"
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(::onPurchasesUpdated)
        .enablePendingPurchases()
        .build()

    private val _subscriptionTier = MutableStateFlow<SubscriptionTier>(SubscriptionTier.Free)
    val subscriptionTier: StateFlow<SubscriptionTier> = _subscriptionTier.asStateFlow()

    init {
        coroutineScope.launch {
            establishConnection()
        }
    }

    private suspend fun establishConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    coroutineScope.launch {
                        queryPurchases()
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // 재연결 로직
                coroutineScope.launch {
                    establishConnection()
                }
            }
        })
    }

    private fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            purchases?.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    coroutineScope.launch {
                        handlePurchase(purchase)
                    }
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
            _subscriptionTier.value = SubscriptionTier.Premium
        }
    }

    private suspend fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        val result = billingClient.queryPurchasesAsync(params)
        result.purchasesList.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                handlePurchase(purchase)
            }
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

        val productDetailsResult = billingClient.queryProductDetailsAsync(productDetailsParams)

        productDetailsResult.productDetailsList?.firstOrNull()?.let { productDetails ->
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

    fun release() {
        billingClient.endConnection()
    }
}