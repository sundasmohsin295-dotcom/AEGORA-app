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
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith

object CommerceManager {

    // UI States for MVI Architecture
    sealed class CommerceState {
        object Idle : CommerceState()
        object ProcessingPurchase : CommerceState()
        object Restoring : CommerceState()
        data class Success(val customerInfo: CustomerInfo) : CommerceState()
        data class Error(val message: String) : CommerceState()
    }

    fun initialize(context: Context) {
        // 1-Tap Anonymous Login: Initializes without a specific App User ID
        val configuredKey = BuildConfig.REVENUECAT_PUBLIC_API_KEY
        val apiKey = if (configuredKey.isNotBlank() && !configuredKey.contains("YOUR_PUBLIC_API_KEY")) {
            configuredKey
        } else {
            "goog_sandbox_judge_aegora_2026"
        }
        if (!Purchases.isConfigured) {
            Purchases.configure(
                PurchasesConfiguration.Builder(context, apiKey).build()
            )
        }
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
                if (customerInfo.entitlements.active.isNotEmpty()) {
                    onResult(CommerceState.Success(customerInfo))
                } else {
                    onResult(CommerceState.Error("No active subscriptions found."))
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
