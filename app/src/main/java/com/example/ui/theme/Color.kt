package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// AEGORA // Multi-Platform Design System — Enterprise Obsidian Palette
// Strict Hackathon Technical Compliance:
// Background: #090A0C (Graphite), Surface Cards: #15171C (Matte Steel), Primary Action: #2962FF (Cobalt)
val AegoraBackground = Color(0xFF090A0C)          // Strict Graphite #090A0C
val AegoraBackgroundGradientEnd = Color(0xFF090A0C)
val AegoraSurface = Color(0xFF15171C)             // Matte Steel #15171C
val AegoraSurfaceElevated = Color(0xFF15171C)     // Matte Steel #15171C
val AegoraSurfaceHighlight = Color(0xFF1E222B)    // Matte Steel highlight #1E222B
val AegoraBorder = Color(0xFF2D313A)              // Slate border #2D313A
val AegoraBorderSubtle = Color(0xFF2D313A)
val AegoraBorderHighlight = Color(0xFF2962FF)     // Corporate Cobalt
val AegoraTextPrimary = Color(0xFFF0F4F8)         // High-contrast clean sans text
val AegoraTextSecondary = Color(0xFF8DA2B5)       // Balanced secondary text
val AegoraTextTertiary = Color(0xFF53677A)        // Low-emphasis caption text

// AEGORA Core & Semantic Color Accents (Enterprise Obsidian)
val SemanticElectricBlue = Color(0xFF2962FF)      // Primary Cobalt Accent #2962FF
val SemanticHome = Color(0xFF2962FF)              // Cobalt #2962FF
val SemanticLearn = Color(0xFF8B5CF6)             // Learn / Cognitive (Violet)
val SemanticPractice = Color(0xFF00E676)          // Practice / Emerald Verification
val SemanticInvestigate = Color(0xFF2962FF)       // Cobalt Investigation
val SemanticAI = Color(0xFF2962FF)                // Cobalt AI
val SemanticProof = Color(0xFF00E676)             // Proof-of-Work / Verification (Green)
val SemanticRoadmap = Color(0xFF14B8A6)           // Roadmap / Progression (Teal)
val SemanticIntel = Color(0xFF2962FF)             // Intel / Feeds (Cobalt)
val SemanticTools = Color(0xFF2962FF)             // Tools / Utilities (Cobalt)
val SemanticWarning = Color(0xFFF59E0B)           // Warnings only (Orange)
val SemanticSuccess = Color(0xFF00E676)           // Verified / Success (Green)

// Legacy & Semantic Compatibility mappings (Aliased to Strict Enterprise Obsidian)
val PureBlack = Color(0xFF090A0C)
val CyberBlack = Color(0xFF090A0C)
val CyberDarkSlate = Color(0xFF15171C)
val CyberCardBg = Color(0xFF15171C)
val CyberCardBorder = Color(0xFF2D313A)
val CyberBackground = Color(0xFF090A0C)
val CyberSurface = Color(0xFF15171C)
val CyberSurfaceVariant = Color(0xFF15171C)
val CyberSurfaceElevated = Color(0xFF15171C)
val CyberBorder = Color(0xFF2D313A)
val CyberBorderSubtle = Color(0xFF2D313A)

// Operational semantic accents mapped to Enterprise Obsidian palette (No cyan/neon glow)
val NeonCyan = Color(0xFF2962FF)                  // Mapped to Cobalt #2962FF
val NeonGreen = Color(0xFF00E676)
val NeonEmerald = Color(0xFF00E676)
val NeonCrimson = Color(0xFFFF1744)
val NeonViolet = Color(0xFF8B5CF6)
val CyberViolet = Color(0xFF8B5CF6)
val NeonPink = Color(0xFFEC4899)
val TerminalAmber = Color(0xFFFFB300)

val CyberCyan = Color(0xFF2962FF)                 // Cobalt replacement for legacy cyan
val CyberCyanDark = Color(0xFF1E40AF)
val CyberBlue = Color(0xFF2962FF)
val CyberIndigo = Color(0xFF3B82F6)
val CyberMagenta = Color(0xFF2962FF)
val CyberPurple = SemanticLearn
val CyberEmerald = SemanticPractice
val CyberGreen = SemanticPractice
val CyberAmber = SemanticRoadmap
val CyberCrimson = SemanticInvestigate
val CyberRed = SemanticInvestigate
val CyberGold = SemanticProof

val AegoraCyanVerified = SemanticHome
val AegoraRedRisk = SemanticInvestigate
val AegoraAmberDecay = SemanticProof

// Container Accents
val VibrantCyanContainer = Color(0xFF102035)
val VibrantCyanOnContainer = SemanticHome
val VibrantPinkContainer = Color(0xFF2E121E)
val VibrantPinkOnContainer = Color(0xFFFDA4AF)
val VibrantPurpleContainer = Color(0xFF1E1735)
val VibrantPurpleOnContainer = SemanticLearn
val VibrantBlueContainer = Color(0xFF0C243C)
val VibrantBlueOnContainer = SemanticHome
val VibrantMintContainer = Color(0xFF0C261E)
val VibrantMintOnContainer = SemanticPractice
val VibrantEmeraldContainer = Color(0xFF0C261E)
val VibrantEmeraldOnContainer = SemanticPractice
val VibrantAmberContainer = Color(0xFF2E220C)
val VibrantAmberOnContainer = SemanticRoadmap
val VibrantNavPill = Color(0xFF121820)

// High-Contrast Typography Tokens
val TextPrimaryDark = AegoraTextPrimary
val TextSecondaryDark = AegoraTextSecondary
val TextTertiaryDark = AegoraTextTertiary
val CodeBackground = Color(0xFF07090D)
val CodeGreen = SemanticPractice
val CodeAmber = SemanticRoadmap

// Light Theme Fallbacks forced to dark tokens
val CyberBackgroundLight = AegoraBackground
val CyberSurfaceLight = AegoraSurface
val CyberSurfaceVariantLight = AegoraSurfaceElevated
val CyberBorderLight = AegoraBorder
val TextPrimaryLight = AegoraTextPrimary
val TextSecondaryLight = AegoraTextSecondary
val TextTertiaryLight = AegoraTextTertiary


