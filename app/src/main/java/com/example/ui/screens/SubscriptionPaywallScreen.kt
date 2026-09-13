package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.models.StoreTransaction

@Composable
fun SubscriptionPaywallScreen(
    onPurchaseSuccess: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current as? Activity
    var availablePackage by remember { mutableStateOf<Package?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isPurchasing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Colors - Exact Enterprise Obsidian palette (Matte Steel and Corporate Cobalt)
    val obsidianBg = Color(0xFF090A0C)
    val matteSteel = Color(0xFF15171C)
    val slateBorder = Color(0xFF2D313A)
    val cobaltBlue = Color(0xFF2962FF)

    // Fetch Offerings from RevenueCat
    LaunchedEffect(Unit) {
        if (!Purchases.isConfigured) {
            errorMessage = "RevenueCat SDK is not initialized. Please configure API key in AegoraApplication."
            isLoading = false
            return@LaunchedEffect
        }

        try {
            Purchases.sharedInstance.getOfferingsWith(
                onError = { error ->
                    errorMessage = error.message
                    isLoading = false
                },
                onSuccess = { offerings ->
                    availablePackage = offerings.current?.monthly
                    isLoading = false
                }
            )
        } catch (e: Throwable) {
            errorMessage = e.message ?: "Failed to retrieve offerings"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(obsidianBg)
            .padding(24.dp)
            .testTag("subscription_paywall_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (onNavigateBack != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("paywall_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back",
                        tint = Color.White
                    )
                }
            }
        }

        Text(
            text = "UPGRADE TO PRO",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("paywall_title")
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Unlock Red Team AI & Unlimited Adversary Duels",
            color = Color(0xFF8A919E),
            fontSize = 14.sp,
            modifier = Modifier.testTag("paywall_subtitle")
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator(
                color = cobaltBlue,
                modifier = Modifier.testTag("paywall_loading_indicator")
            )
        } else if (availablePackage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = matteSteel),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, slateBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .testTag("paywall_package_card")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = availablePackage!!.product.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("package_title")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = availablePackage!!.product.price.formatted,
                        color = Color(0xFF00E676),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("package_price")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (context != null && Purchases.isConfigured && availablePackage != null && !isPurchasing) {
                                isPurchasing = true
                                errorMessage = null
                                val params = PurchaseParams.Builder(context, availablePackage!!).build()
                                Purchases.sharedInstance.purchase(
                                    params,
                                    object : PurchaseCallback {
                                        override fun onCompleted(
                                            storeTransaction: StoreTransaction,
                                            customerInfo: com.revenuecat.purchases.CustomerInfo
                                        ) {
                                            isPurchasing = false
                                            com.example.subscription.AegoraSubscriptionRepository.updateFromCustomerInfo(customerInfo)
                                            if (customerInfo.entitlements["pro_tier"]?.isActive == true) {
                                                onPurchaseSuccess()
                                            }
                                        }

                                        override fun onError(error: PurchasesError, userCancelled: Boolean) {
                                            isPurchasing = false
                                            if (!userCancelled) {
                                                errorMessage = error.message
                                            } else {
                                                errorMessage = null // Graceful sandbox cancellation handling
                                            }
                                        }
                                    }
                                )
                            } else if (context == null || !Purchases.isConfigured) {
                                errorMessage = "Purchases client instance is not ready."
                            }
                        },
                        enabled = !isPurchasing && availablePackage != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("authorize_upgrade_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cobaltBlue,
                            disabledContainerColor = cobaltBlue.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isPurchasing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("AUTHORIZE UPGRADE", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = matteSteel),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, slateBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Packages currently unavailable. ${errorMessage.orEmpty()}",
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.testTag("paywall_error_text")
                    )
                }
            }
        }
    }
}

@Composable
fun SubscriptionPaywallScreen(onPurchaseSuccess: () -> Unit) {
    SubscriptionPaywallScreen(onPurchaseSuccess = onPurchaseSuccess, onNavigateBack = null)
}
