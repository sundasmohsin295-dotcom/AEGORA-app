package com.aegora.billing

import android.app.Activity
import android.content.Context
import com.aegora.BuildConfig
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.getCustomerInfoWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CommerceManager {

    // Global real-time stream of CustomerInfo for instant entitlement synchronization
    private val _customerInfoStream = MutableStateFlow<CustomerInfo?>(null)
    val customerInfoStream: StateFlow<CustomerInfo?> = _customerInfoStream.asStateFlow()

    // UI States for MVI Architecture
    sealed class CommerceState {
        object Idle : CommerceState()
        object ProcessingPurchase : CommerceState()
        object Restoring : CommerceState()
        data class Success(val customerInfo: CustomerInfo) : CommerceState()
        data class Error(val message: String) : CommerceState()
    }

    fun initialize(context: Context) {
        // Explicitly enable DEBUG log level to monitor Google Play Billing sandbox handshake
        Purchases.logLevel = LogLevel.DEBUG

        // 1-Tap Anonymous Login: Initializes without a specific App User ID
        val apiKey = BuildConfig.REVENUECAT_PUBLIC_API_KEY
        if (apiKey.isNotBlank() && !Purchases.isConfigured) {
            Purchases.configure(
                PurchasesConfiguration.Builder(context, apiKey).build()
            )
        }

        if (Purchases.isConfigured) {
            // Instant Entitlement Sync: Attach UpdatedCustomerInfoListener
            Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { customerInfo ->
                syncCustomerInfo(customerInfo)
            }

            // Prime the initial customer info state
            Purchases.sharedInstance.getCustomerInfoWith(
                onError = { /* offline fallback handled by cache */ },
                onSuccess = { customerInfo ->
                    syncCustomerInfo(customerInfo)
                }
            )
        }
    }

    private fun syncCustomerInfo(customerInfo: CustomerInfo) {
        _customerInfoStream.value = customerInfo
        com.example.subscription.AegoraSubscriptionRepository.updateFromCustomerInfo(customerInfo)
    }

    fun purchase(activity: Activity, rcPackage: Package, onResult: (CommerceState) -> Unit) {
        onResult(CommerceState.ProcessingPurchase)
        val params = PurchaseParams.Builder(activity, rcPackage).build()
        Purchases.sharedInstance.purchaseWith(
            params,
            onError = { error, userCancelled ->
                if (!userCancelled) {
                    onResult(CommerceState.Error(error.message))
                } else {
                    onResult(CommerceState.Idle)
                }
            },
            onSuccess = { storeTransaction, customerInfo ->
                syncCustomerInfo(customerInfo)
                onResult(CommerceState.Success(customerInfo))
            }
        )
    }

    fun restorePurchases(onResult: (CommerceState) -> Unit) {
        onResult(CommerceState.Restoring)
        Purchases.sharedInstance.restorePurchasesWith(
            onError = { error ->
                onResult(CommerceState.Error(error.message))
            },
            onSuccess = { customerInfo ->
                syncCustomerInfo(customerInfo)
                if (customerInfo.entitlements.active.isNotEmpty()) {
                    onResult(CommerceState.Success(customerInfo))
                } else {
                    onResult(CommerceState.Error("No active PRO or CAREER operations found."))
                }
            }
        )
    }

    fun purchaseTier(activity: Activity, tierIdentifier: String, onResult: (CommerceState) -> Unit) {
        onResult(CommerceState.ProcessingPurchase)
        if (!Purchases.isConfigured) {
            onResult(CommerceState.Idle)
            return
        }
        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                onResult(CommerceState.Error(error.message))
            },
            onSuccess = { offerings ->
                val targetPackage = offerings.current?.availablePackages?.find { pkg ->
                    pkg.identifier.contains(tierIdentifier, ignoreCase = true) ||
                    (tierIdentifier.contains("pro", ignoreCase = true) && pkg.packageType == com.revenuecat.purchases.PackageType.MONTHLY) ||
                    (tierIdentifier.contains("career", ignoreCase = true) && pkg.packageType == com.revenuecat.purchases.PackageType.ANNUAL)
                } ?: offerings.current?.availablePackages?.firstOrNull()

                if (targetPackage != null) {
                    purchase(activity, targetPackage, onResult)
                } else {
                    onResult(CommerceState.Error("Package not found for tier: $tierIdentifier"))
                }
            }
        )
    }
}
