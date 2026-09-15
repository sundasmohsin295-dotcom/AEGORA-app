package com.aegora.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aegora.billing.CommerceManager

@Composable
fun PremiumUpgradeScreen(
    commerceState: CommerceManager.CommerceState,
    onPurchaseRequested: (String) -> Unit, // Pass tier identifier
    onRestoreRequested: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    
    // Enterprise Obsidian Palette
    val bgCanvas = Color(0xFF050B14)
    val matteSteel = Color(0xFF0B1528)
    val mutedSlate = Color(0xFF8CA3C7)
    val cobaltBlue = Color(0xFF3B82F6)
    val emerald = Color(0xFF34D399)
    val borderHairline = Color(0xFF1E3A5F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgCanvas)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "VERIFIED OPERATIONS",
            color = cobaltBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        
        Text(
            text = "Unlock Your Full Potential",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
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
                    onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
                )
            }
            item {
                TierCard(
                    title = "PRO",
                    price = "$4.99 /month",
                    features = "• Unlimited Duels\n• Deep AI Autopsies\n• Live Telemetry Streams",
                    borderColor = cobaltBlue,
                    buttonColor = cobaltBlue,
                    buttonText = "SUBSCRIBE",
                    isLoading = commerceState == CommerceManager.CommerceState.ProcessingPurchase,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPurchaseRequested("pro_tier")
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
                    buttonText = "VERIFIED TALENT",
                    isLoading = commerceState == CommerceManager.CommerceState.ProcessingPurchase,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPurchaseRequested("career_tier")
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
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onRestoreRequested()
            },
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        ) {
            if (commerceState == CommerceManager.CommerceState.Restoring) {
                CircularProgressIndicator(color = cobaltBlue, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            } else {
                Text("Restore Previous Purchases", color = mutedSlate, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun TierCard(
    title: String, price: String, features: String,
    borderColor: Color, buttonColor: Color, buttonText: String,
    isLoading: Boolean, onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
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
                onClick = { onClick() }
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0B1528),
        border = BorderStroke(1.dp, borderColor)
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
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(buttonText, color = if (buttonColor == Color(0xFF0B1528)) Color.White else Color(0xFF050B14), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
