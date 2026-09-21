package com.aegora.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aegora.billing.CommerceManager
import kotlinx.coroutines.delay

@Composable
fun PremiumUpgradeScreen(
    commerceState: CommerceManager.CommerceState = CommerceManager.CommerceState.Idle,
    onPurchaseRequested: (String) -> Unit = {},
    onRestoreRequested: () -> Unit = {},
    onNavigateBack: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current

    // BACK-STACK RESILIENCE: BackHandler safely dismisses paywall instead of exiting app
    if (onNavigateBack != null) {
        BackHandler(enabled = true) {
            onNavigateBack()
        }
    }

    // THE MASH TEST: Immediate debounce and loading state tracking
    var localProcessingTier by remember { mutableStateOf<String?>(null) }

    // Reset local debounce when commerce operation finishes
    LaunchedEffect(commerceState) {
        if (commerceState !is CommerceManager.CommerceState.ProcessingPurchase) {
            localProcessingTier = null
        }
    }

    // Safety timeout: auto-clear debounce if no commerce response arrives within 4 seconds
    LaunchedEffect(localProcessingTier) {
        if (localProcessingTier != null) {
            kotlinx.coroutines.delay(4000)
            localProcessingTier = null
        }
    }

    val isProLoading = localProcessingTier == "pro_tier" ||
        (commerceState is CommerceManager.CommerceState.ProcessingPurchase && localProcessingTier == "pro_tier")
    val isCareerLoading = localProcessingTier == "career_tier" ||
        (commerceState is CommerceManager.CommerceState.ProcessingPurchase && localProcessingTier == "career_tier")
    val isRestoring = commerceState is CommerceManager.CommerceState.Restoring
    val isAnyOperationActive = isProLoading || isCareerLoading || isRestoring

    // Enterprise Obsidian Palette
    val bgCanvas = Color(0xFF050B14)
    val matteSteel = Color(0xFF0B1528)
    val mutedSlate = Color(0xFF8CA3C7)
    val cobaltBlue = Color(0xFF3B82F6)
    val emerald = Color(0xFF34D399)
    val borderHairline = Color(0xFF1E3A5F)

    // Snackbar Host & Friendly Error Mapping
    val snackbarHostState = remember { SnackbarHostState() }

    // Success state tracking for elite micro-interaction
    var isPurchaseSuccessVerified by remember { mutableStateOf(false) }

    LaunchedEffect(commerceState) {
        when (commerceState) {
            is CommerceManager.CommerceState.Success -> {
                // Elite Success Micro-Interaction
                isPurchaseSuccessVerified = true
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                snackbarHostState.showSnackbar("Cryptographic Entitlement Granted: Welcome to PRO.")
                // Wait 1 second before dismissing to let user savor the verified state
                delay(1000)
                onNavigateBack?.invoke()
            }
            is CommerceManager.CommerceState.Error -> {
                val rawMsg = commerceState.message.lowercase()
                val friendlyMsg = when {
                    rawMsg.contains("network") || rawMsg.contains("internet") || rawMsg.contains("connection") || rawMsg.contains("offline") || rawMsg.contains("timeout") -> {
                        "Secure connection lost. Check your internet."
                    }
                    rawMsg.contains("restore") || rawMsg.contains("no active") || rawMsg.contains("not found") -> {
                        "No active PRO or CAREER operations found."
                    }
                    else -> {
                        "Secure operation could not be completed. Please retry."
                    }
                }
                snackbarHostState.showSnackbar(friendlyMsg)
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgCanvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        // Top Bar with Optional Close Button for Smooth Modal Dismissal
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VERIFIED OPERATIONS",
                color = cobaltBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            if (onNavigateBack != null) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNavigateBack()
                    },
                    modifier = Modifier.size(36.dp).testTag("paywall_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss Paywall",
                        tint = mutedSlate
                    )
                }
            }
        }

        Text(
            text = "Unlock Your Full Potential",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )

        // Bento Box Grid
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(1),
            verticalItemSpacing = 16.dp,
            modifier = Modifier.weight(1f)
        ) {
            item {
                TierCard(
                    title = "FREE",
                    price = "$0 /forever",
                    features = "• 2 Daily Async Duels\n• Basic Telemetry",
                    borderColor = borderHairline,
                    buttonColor = matteSteel,
                    buttonText = "CURRENT PLAN",
                    isLoading = false,
                    enabled = !isAnyOperationActive,
                    onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
                )
            }
            item {
                TierCard(
                    title = "PRO",
                    price = "$4.99 /month",
                    features = "• Unlimited Duels\n• Deep AI Autopsies\n• Live Telemetry Streams",
                    borderColor = if (isPurchaseSuccessVerified && (localProcessingTier == "pro_tier" || localProcessingTier == null)) emerald else cobaltBlue,
                    buttonColor = if (isPurchaseSuccessVerified && (localProcessingTier == "pro_tier" || localProcessingTier == null)) emerald else cobaltBlue,
                    buttonText = if (isPurchaseSuccessVerified && (localProcessingTier == "pro_tier" || localProcessingTier == null)) "[OK] PAYMENT VERIFIED" else "SUBSCRIBE",
                    isLoading = isProLoading && !isPurchaseSuccessVerified,
                    isVerifiedSuccess = isPurchaseSuccessVerified && (localProcessingTier == "pro_tier" || localProcessingTier == null),
                    enabled = !isAnyOperationActive && !isPurchaseSuccessVerified,
                    onClick = {
                        if (!isAnyOperationActive && !isPurchaseSuccessVerified) {
                            localProcessingTier = "pro_tier"
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onPurchaseRequested("pro_tier")
                        }
                    }
                )
            }
            item {
                TierCard(
                    title = "CAREER",
                    price = "$9.99 /month",
                    features = "• Biometric Skill Passport\n• Corporate Pipeline Access",
                    borderColor = emerald,
                    buttonColor = emerald,
                    buttonText = if (isPurchaseSuccessVerified && localProcessingTier == "career_tier") "[OK] PAYMENT VERIFIED" else "VERIFIED TALENT",
                    isLoading = isCareerLoading && !isPurchaseSuccessVerified,
                    isVerifiedSuccess = isPurchaseSuccessVerified && localProcessingTier == "career_tier",
                    enabled = !isAnyOperationActive && !isPurchaseSuccessVerified,
                    onClick = {
                        if (!isAnyOperationActive && !isPurchaseSuccessVerified) {
                            localProcessingTier = "career_tier"
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onPurchaseRequested("career_tier")
                        }
                    }
                )
            }
        }

        // COMPLIANCE SECTION
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "PRO: $4.99/month. CAREER: $9.99/month. Subscriptions auto-renew until cancelled. Manage or cancel anytime in Google Play > Subscriptions.",
            color = mutedSlate,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Terms of Service", color = mutedSlate, fontSize = 10.sp)
            Text("Privacy Policy", color = mutedSlate, fontSize = 10.sp)
        }

        // RESTORE PURCHASES
        TextButton(
            onClick = {
                if (!isAnyOperationActive) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRestoreRequested()
                }
            },
            enabled = !isAnyOperationActive,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp).testTag("restore_purchases_button")
        ) {
            if (isRestoring) {
                CircularProgressIndicator(color = cobaltBlue, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            } else {
                Text("Restore Previous Purchases", color = if (isAnyOperationActive) mutedSlate.copy(alpha = 0.5f) else mutedSlate, fontSize = 12.sp)
            }
        }
    }

    // Sleek Dark-Themed Snackbar with Cobalt Blue text
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(16.dp)
            .testTag("paywall_snackbar_host")
    ) { snackbarData ->
        Snackbar(
            modifier = Modifier.border(BorderStroke(1.dp, borderHairline), RoundedCornerShape(10.dp)),
            containerColor = matteSteel,
            contentColor = cobaltBlue,
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = snackbarData.visuals.message,
                color = cobaltBlue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}
}

@Composable
fun TierCard(
    title: String,
    price: String,
    features: String,
    borderColor: Color,
    buttonColor: Color,
    buttonText: String,
    isLoading: Boolean,
    isVerifiedSuccess: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isVerifiedSuccess) 1.02f else if (isPressed && enabled && !isLoading) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "TierCardScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled && !isLoading && !isVerifiedSuccess,
                onClick = {
                    if (enabled && !isLoading && !isVerifiedSuccess) {
                        onClick()
                    }
                }
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0B1528),
        border = BorderStroke(if (isVerifiedSuccess) 2.dp else 1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(price, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Text(
                text = features,
                color = Color(0xFF8CA3C7),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 12.dp, bottom = 20.dp)
            )
            
            Button(
                onClick = {
                    if (enabled && !isLoading && !isVerifiedSuccess) {
                        onClick()
                    }
                },
                enabled = (enabled && !isLoading) || isVerifiedSuccess,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = if (isVerifiedSuccess) buttonColor else buttonColor.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("tier_button_${title.lowercase()}")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = buttonText,
                        color = if (isVerifiedSuccess) Color.Black else if (buttonColor == Color(0xFF0B1528)) Color.White else Color(0xFF050B14),
                        fontWeight = FontWeight.Bold,
                        fontFamily = if (isVerifiedSuccess) FontFamily.Monospace else FontFamily.Default,
                        letterSpacing = if (isVerifiedSuccess) 1.sp else 0.sp
                    )
                }
            }
        }
    }
}
